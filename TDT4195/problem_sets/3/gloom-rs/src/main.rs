// Uncomment these following global attributes to silence most warnings of "low" interest:
/*
#![allow(dead_code)]
#![allow(non_snake_case)]
#![allow(unreachable_code)]
#![allow(unused_mut)]
#![allow(unused_unsafe)]
#![allow(unused_variables)]
*/


extern crate nalgebra_glm as glm;
use std::{ ptr };
use std::thread;
use std::sync::{Mutex, Arc, RwLock};

mod shader;
mod util;
mod mesh;
mod gl_util;
mod toolbox;
mod chase_camera;

mod scene_graph;
use scene_graph::SceneNode;


use glutin::event::{Event, WindowEvent, DeviceEvent, KeyboardInput, ElementState::{Pressed, Released}, VirtualKeyCode::{self, *}};
use glutin::event_loop::ControlFlow;


// initial window size
const INITIAL_SCREEN_W: u32 = 800;
const INITIAL_SCREEN_H: u32 = 600;

unsafe fn draw_scene(node: &scene_graph::SceneNode, view_projection_matrix: &glm::Mat4, transformation_so_far: &glm::Mat4, camera_position: &glm::Vec3) {

    let mut model_matrix = glm::identity();
    model_matrix = glm::translation(&-node.reference_point) * model_matrix;
    model_matrix = glm::rotation(node.rotation.x, &glm::vec3(1.0, 0.0, 0.0)) * model_matrix;
    model_matrix = glm::rotation(node.rotation.y, &glm::vec3(0.0, 1.0, 0.0)) * model_matrix;
    model_matrix = glm::rotation(node.rotation.z, &glm::vec3(0.0, 0.0, 1.0)) * model_matrix;
    model_matrix = glm::translation(&node.position) * model_matrix;
    model_matrix = glm::translation(&node.reference_point) * model_matrix;

    let acc_model_matrix = transformation_so_far * model_matrix;

    // Draw
    if node.index_count != -1 {
        if node.vao_id == 0 {
            panic!("Node has index count but no VAO ID !?!");
        }
        let local_to_world = view_projection_matrix * acc_model_matrix;
        gl::BindVertexArray(node.vao_id);
        gl::UniformMatrix4fv(gl_util::VERT_LOC_PROJ_UNIFORM, 1, gl::FALSE, local_to_world.as_ptr());
        gl::UniformMatrix4fv(gl_util::VERT_LOC_MODEL_MATRIX_UNIFORM, 1, gl::FALSE, acc_model_matrix.as_ptr());
        gl::Uniform3fv(gl_util::VERT_LOC_VIEW_POS, 1, camera_position.as_ptr());
        gl::DrawElements(gl::TRIANGLES, node.index_count, gl::UNSIGNED_INT, ptr::null());
    }

    for &child in &node.children {
        draw_scene(&*child, view_projection_matrix, &acc_model_matrix, camera_position);
    }
}


fn main() {
    // Set up the necessary objects to deal with windows and event handling
    let el = glutin::event_loop::EventLoop::new();
    let wb = glutin::window::WindowBuilder::new()
        .with_title("Gloom-rs")
        .with_resizable(true)
        .with_inner_size(glutin::dpi::LogicalSize::new(INITIAL_SCREEN_W, INITIAL_SCREEN_H));
    let cb = glutin::ContextBuilder::new()
        .with_vsync(true);
    let windowed_context = cb.build_windowed(wb, &el).unwrap();
    // Uncomment these if you want to use the mouse for controls, but want it to be confined to the screen and/or invisible.
    // windowed_context.window().set_cursor_grab(true).expect("failed to grab cursor");
    // windowed_context.window().set_cursor_visible(false);

    // Set up a shared vector for keeping track of currently pressed keys
    let arc_pressed_keys = Arc::new(Mutex::new(Vec::<VirtualKeyCode>::with_capacity(10)));
    // Make a reference of this vector to send to the render thread
    let pressed_keys = Arc::clone(&arc_pressed_keys);

    // Set up shared tuple for tracking mouse movement between frames
    let arc_mouse_delta = Arc::new(Mutex::new((0f32, 0f32)));
    // Make a reference of this tuple to send to the render thread
    let mouse_delta = Arc::clone(&arc_mouse_delta);

    // Set up shared tuple for tracking changes to the window size
    let arc_window_size = Arc::new(Mutex::new((INITIAL_SCREEN_W, INITIAL_SCREEN_H, false)));
    // Make a reference of this tuple to send to the render thread
    let window_size = Arc::clone(&arc_window_size);

    // Spawn a separate thread for rendering, so event handling doesn't block rendering
    let render_thread = thread::spawn(move || {
        // Acquire the OpenGL Context and load the function pointers.
        // This has to be done inside of the rendering thread, because
        // an active OpenGL context cannot safely traverse a thread boundary
        let context = unsafe {
            let c = windowed_context.make_current().unwrap();
            gl::load_with(|symbol| c.get_proc_address(symbol) as *const _);
            c
        };

        let mut window_aspect_ratio = INITIAL_SCREEN_W as f32 / INITIAL_SCREEN_H as f32;

        // Set up openGL
        unsafe {
            gl::Enable(gl::DEPTH_TEST);
            gl::DepthFunc(gl::LESS);
            gl::Enable(gl::CULL_FACE);
            gl::Disable(gl::MULTISAMPLE);
            gl::Enable(gl::BLEND);
            gl::BlendFunc(gl::SRC_ALPHA, gl::ONE_MINUS_SRC_ALPHA);
            gl::Enable(gl::DEBUG_OUTPUT_SYNCHRONOUS);
            gl::DebugMessageCallback(Some(util::debug_callback), ptr::null());

            // Print some diagnostics
            println!("{}: {}", util::get_gl_string(gl::VENDOR), util::get_gl_string(gl::RENDERER));
            println!("OpenGL\t: {}", util::get_gl_string(gl::VERSION));
            println!("GLSL\t: {}", util::get_gl_string(gl::SHADING_LANGUAGE_VERSION));
        }

        // Load obj's 
        let terrain_mesh = mesh::Terrain::load("./resources/lunarsurface.obj");
        let terrain_vao = unsafe { gl_util::create_vao_from_mesh(&terrain_mesh) };

        let helicopter = mesh::Helicopter::load("./resources/helicopter.obj");
        let heli_body = unsafe { gl_util::create_vao_from_mesh(&helicopter.body) };
        let heli_door = unsafe { gl_util::create_vao_from_mesh(&helicopter.door) };
        let heli_main_rotor = unsafe { gl_util::create_vao_from_mesh(&helicopter.main_rotor) };
        let heli_tail_rotor = unsafe { gl_util::create_vao_from_mesh(&helicopter.tail_rotor) };

        // Set up scene graph
        let mut helis = Vec::new();

        // Helicopter setup
        let n_helis = 1;
        for _ in 0..n_helis {
            let door = SceneNode::from_vao(heli_door, helicopter.door.index_count);
            let mut main_rotor = SceneNode::from_vao(heli_main_rotor, helicopter.main_rotor.index_count);
            let mut tail_rotor = SceneNode::from_vao(heli_tail_rotor, helicopter.tail_rotor.index_count);
            tail_rotor.reference_point = glm::vec3(0.35, 2.3, 10.4);

            let mut body = SceneNode::from_vao(heli_body, helicopter.body.index_count);
            body.reference_point = glm::vec3(0.0, 0.0, -2.0);
            body.add_child(&door);
            body.add_child(&main_rotor);
            body.add_child(&tail_rotor);

            // Create helicopter root node and attach body
            let mut heli_node = SceneNode::new();
            heli_node.add_child(&body);
            helis.push(heli_node);
        }

        let mut terrain_node = SceneNode::from_vao(terrain_vao, terrain_mesh.index_count);
        for heli in &helis {
            terrain_node.add_child(heli);
        }

        let mut scene_graph_root = SceneNode::new();
        scene_graph_root.add_child(&terrain_node);


        // Load shaders
        let shader = unsafe { 
            shader::ShaderBuilder::new()
                .attach_file("./shaders/combination.vert")
                .attach_file("./shaders/phong.frag")
                .link()
        };

        // The main rendering loop
        let first_frame_time = std::time::Instant::now();
        let mut previous_frame_time = first_frame_time;
        
        // Camera variables
        let speed_rot = 3.0_f32;

        // Old camera variables
        /*
        let mut x = 0.0_f32;
        let mut y = 0.0_f32;
        let mut z = 0.0_f32;
        let mut x_rot = 0.0_f32;
        let mut y_rot = 0.0_f32;
        */

        // Chase camera 
        let mut chase_camera = chase_camera::ChaseCamera::new(
            15.0,
            5.0,
        );

        let heli: &mut SceneNode = &mut helis[0];
        let heli_body: &mut SceneNode = heli.get_child(0);
        let mut speed_heli = 20_f32;
        let speed_rot_heli = 0.75_f32;

        loop {
            // Compute time passed since the previous frame and since the start of the program
            let now = std::time::Instant::now();
            let elapsed = now.duration_since(first_frame_time).as_secs_f32();
            let delta_time = now.duration_since(previous_frame_time).as_secs_f32();
            previous_frame_time = now;

            // Handle resize events
            if let Ok(mut new_size) = window_size.lock() {
                if new_size.2 {
                    context.resize(glutin::dpi::PhysicalSize::new(new_size.0, new_size.1));
                    window_aspect_ratio = new_size.0 as f32 / new_size.1 as f32;
                    (*new_size).2 = false;
                    println!("Window was resized to {}x{}", new_size.0, new_size.1);
                    unsafe { gl::Viewport(0, 0, new_size.0 as i32, new_size.1 as i32); }
                }
            }

            // Forward movement
            let forward = glm::vec3(
                -heli_body.rotation.y.sin() * heli_body.rotation.x.cos(),
                heli_body.rotation.x.sin(),
                -heli_body.rotation.y.cos() * heli_body.rotation.x.cos()
            );

            speed_heli = speed_heli.clamp(10.0, 100.0);
            heli_body.position += forward * speed_heli * delta_time;

            // Rotor rotation
            heli_body.get_child(1).rotation.y += speed_heli / 5.0 * delta_time; // Main rotor
            heli_body.get_child(2).rotation.x += 1.5 * speed_heli / 5.0; // Tail rotor

            // Handle keyboard input
            if let Ok(keys) = pressed_keys.lock() {
                for key in keys.iter() {
                    match key {
                        // The `VirtualKeyCode` enum is defined here:
                        //    https://docs.rs/winit/0.25.0/winit/event/enum.VirtualKeyCode.html

                        VirtualKeyCode::W => {
                            //z += speed * delta_time;
                            speed_heli += 10.0 * delta_time;
                        }
                        VirtualKeyCode::S => {
                            //z -= speed * delta_time;
                            speed_heli -= 10.0 * delta_time;
                        }

                        VirtualKeyCode::A => {
                            //x += speed * delta_time;
                            heli_body.rotation.y += speed_rot_heli * delta_time;
                        }
                        VirtualKeyCode::D => {
                            //x -= speed * delta_time;
                            heli_body.rotation.y -= speed_rot_heli * delta_time;
                        }
                        
                        VirtualKeyCode::Space => {
                            // y -= speed * delta_time;
                            heli_body.rotation.x += speed_rot_heli * delta_time;
                        }
                        VirtualKeyCode::LShift => {
                            // y += speed * delta_time;
                            heli_body.rotation.x -= speed_rot_heli * delta_time;
                        }

                        /*
                        VirtualKeyCode::Left => {
                            y_rot -= speed_rot * delta_time;
                        }
                        VirtualKeyCode::Right => {
                            y_rot += speed_rot * delta_time;
                        }
                        VirtualKeyCode::Up => {
                            x_rot -= speed_rot * delta_time;
                        }
                        VirtualKeyCode::Down => {
                            x_rot += speed_rot * delta_time;
                        }
                        */

                        // default handler:
                        _ => { }
                    }
                }
            }

            // Handle mouse movement. delta contains the x and y movement of the mouse since last frame in pixels
            if let Ok(mut delta) = mouse_delta.lock() {

                // == // Optionally access the accumulated mouse movement between
                // == // frames here with `delta.0` and `delta.1`

                *delta = (0.0, 0.0); // reset when done
            }

            let perspective_projection: glm::Mat4 =
                glm::perspective(
                window_aspect_ratio,
                1.2, // Field of View
                1.0, // Near plane
                1000.0 // Far plane
            );

            // Update the chase camera
            let heli_forward = glm::vec3(
                -heli_body.rotation.y.sin() * heli_body.rotation.x.cos(),
                heli_body.rotation.x.sin(),
                -heli_body.rotation.y.cos() * heli_body.rotation.x.cos()
            );
            chase_camera.set_target(heli_body.position, glm::normalize(&heli_forward));
            let view_matrix = chase_camera.update();

            // Old view matrix calculations 
            /*
            let flip: glm::Mat4 = glm::translation(&glm::vec3(0.0, 0.0, -2.0));
            let translation: glm::Mat4 = glm::translation(&glm::vec3(x, y, z));
            let rotation: glm::Mat4 = glm::rotation(x_rot, &glm::vec3(1.0, 0.0, 0.0)) 
                                    * glm::rotation(y_rot, &glm::vec3(0.0, 1.0, 0.0));
            let view_projection: glm::Mat4 = perspective_projection * flip * translation * rotation;
            */
            let view_projection: glm::Mat4 = perspective_projection * view_matrix;

            // Helicopter animation
            /*
            let helis_len = helis.len(); 
            for (i, heli) in helis.iter_mut().enumerate() {
                let time = (elapsed + (i as f32 / helis_len as f32) * 8.0) * 0.5;
                let heading = toolbox::simple_heading_animation(time);

                let heli_body = heli.get_child(0);
                heli_body.position = glm::vec3(heading.x, 3.0, heading.z);
                heli_body.rotation = glm::vec3(heading.pitch, heading.yaw, heading.roll);
            }
            */

            // Render the scene
            unsafe {
                // Clear the color and depth buffers
                gl::ClearColor(0.035, 0.046, 0.078, 1.0); // night sky
                gl::Clear(gl::COLOR_BUFFER_BIT | gl::DEPTH_BUFFER_BIT);

                shader.activate();
                let mat4identity: glm::Mat4 = glm::identity();
                draw_scene(&scene_graph_root, &view_projection, &mat4identity, &chase_camera.position);
            }

            // Display the new color buffer on the display
            context.swap_buffers().unwrap(); // we use "double buffering" to avoid artifacts
        }
    });


    // == //
    // == // From here on down there are only internals.
    // == //


    // Keep track of the health of the rendering thread
    let render_thread_healthy = Arc::new(RwLock::new(true));
    let render_thread_watchdog = Arc::clone(&render_thread_healthy);
    thread::spawn(move || {
        if !render_thread.join().is_ok() {
            if let Ok(mut health) = render_thread_watchdog.write() {
                println!("Render thread panicked!");
                *health = false;
            }
        }
    });

    // Start the event loop -- This is where window events are initially handled
    el.run(move |event, _, control_flow| {
        *control_flow = ControlFlow::Wait;

        // Terminate program if render thread panics
        if let Ok(health) = render_thread_healthy.read() {
            if *health == false {
                *control_flow = ControlFlow::Exit;
            }
        }

        match event {
            Event::WindowEvent { event: WindowEvent::Resized(physical_size), .. } => {
                println!("New window size received: {}x{}", physical_size.width, physical_size.height);
                if let Ok(mut new_size) = arc_window_size.lock() {
                    *new_size = (physical_size.width, physical_size.height, true);
                }
            }
            Event::WindowEvent { event: WindowEvent::CloseRequested, .. } => {
                *control_flow = ControlFlow::Exit;
            }
            // Keep track of currently pressed keys to send to the rendering thread
            Event::WindowEvent { event: WindowEvent::KeyboardInput {
                    input: KeyboardInput { state: key_state, virtual_keycode: Some(keycode), .. }, .. }, .. } => {

                if let Ok(mut keys) = arc_pressed_keys.lock() {
                    match key_state {
                        Released => {
                            if keys.contains(&keycode) {
                                let i = keys.iter().position(|&k| k == keycode).unwrap();
                                keys.remove(i);
                            }
                        },
                        Pressed => {
                            if !keys.contains(&keycode) {
                                keys.push(keycode);
                            }
                        }
                    }
                }

                // Handle Escape and Q keys separately
                match keycode {
                    Escape => { *control_flow = ControlFlow::Exit; }
                    Q      => { *control_flow = ControlFlow::Exit; }
                    _      => { }
                }
            }
            Event::DeviceEvent { event: DeviceEvent::MouseMotion { delta }, .. } => {
                // Accumulate mouse movement
                if let Ok(mut position) = arc_mouse_delta.lock() {
                    *position = (position.0 + delta.0 as f32, position.1 + delta.1 as f32);
                }
            }
            _ => { }
        }
    });
}
