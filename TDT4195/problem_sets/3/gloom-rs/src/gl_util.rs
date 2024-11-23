use std::{ mem, ptr, os::raw::c_void };
use crate::mesh;

// VAO attribute indices
pub const VERT_LOC_POSITION: u32 = 0;
pub const VERT_LOC_COLOR: u32 = 1;
pub const VERT_LOC_NORMAL: u32 = 2;
pub const VERT_LOC_PROJ_UNIFORM: i32 = 3;
pub const VERT_LOC_MODEL_MATRIX_UNIFORM: i32 = 4;

pub const VERT_LOC_VIEW_POS: i32 = 5;


// == // Helper functions to make interacting with OpenGL a little bit prettier. You *WILL* need these! // == //

// Get the size of an arbitrary array of numbers measured in bytes
// Example usage:  byte_size_of_array(my_array)
fn byte_size_of_array<T>(val: &[T]) -> isize {
    std::mem::size_of_val(&val[..]) as isize
}

// Get the OpenGL-compatible pointer to an arbitrary array of numbers
// Example usage:  pointer_to_array(my_array)
fn pointer_to_array<T>(val: &[T]) -> *const c_void {
    &val[0] as *const T as *const c_void
}

// Get the size of the given type in bytes
// Example usage:  size_of::<u64>()
fn size_of<T>() -> i32 {
    mem::size_of::<T>() as i32
}

// Get an offset in bytes for n units of type T, represented as a relative pointer
// Example usage:  offset::<u64>(4)
fn offset<T>(n: u32) -> *const c_void {
    (n * mem::size_of::<T>() as u32) as *const T as *const c_void
}

pub unsafe fn create_vao_from_mesh(mesh: &mesh::Mesh) -> u32 {
    // Generate and bind a VAO
    let mut array = 0;
    gl::GenVertexArrays(1, &mut array);
    gl::BindVertexArray(array);

    // Generate and bind a VBO
    let mut buffer = 0;
    gl::GenBuffers(1, &mut buffer);
    gl::BindBuffer(gl::ARRAY_BUFFER, buffer);
    // Fill the VBO with data
    gl::BufferData(gl::ARRAY_BUFFER, byte_size_of_array(&mesh.vertices), pointer_to_array(&mesh.vertices), gl::STATIC_DRAW);

    // Configure a VAP for the data and enable it
    gl::VertexAttribPointer(VERT_LOC_POSITION, 3, gl::FLOAT, gl::FALSE, 3 * size_of::<f32>(), ptr::null());
    gl::EnableVertexAttribArray(VERT_LOC_POSITION);

    // Generate and bind a VBO for the colors
    let mut color_buffer = 0;
    gl::GenBuffers(1, &mut color_buffer);
    gl::BindBuffer(gl::ARRAY_BUFFER, color_buffer);
    gl::BufferData(gl::ARRAY_BUFFER, byte_size_of_array(&mesh.colors), pointer_to_array(&mesh.colors), gl::STATIC_DRAW);

    // Configure a VAP for the colors and enable it
    gl::VertexAttribPointer(VERT_LOC_COLOR, 4, gl::FLOAT, gl::FALSE, 4 * size_of::<f32>(), ptr::null());
    gl::EnableVertexAttribArray(VERT_LOC_COLOR);

    // Generate and bind a VBO for the normals
    let mut normal_buffer = 0;
    gl::GenBuffers(1, &mut normal_buffer);
    gl::BindBuffer(gl::ARRAY_BUFFER, normal_buffer);
    gl::BufferData(gl::ARRAY_BUFFER, byte_size_of_array(&mesh.normals), pointer_to_array(&mesh.normals), gl::STATIC_DRAW);

    // Configure a VAP for the normals and enable it
    gl::VertexAttribPointer(VERT_LOC_NORMAL, 3, gl::FLOAT, gl::FALSE, 3 * size_of::<f32>(), ptr::null());
    gl::EnableVertexAttribArray(VERT_LOC_NORMAL);

    // Generate a IBO and bind it
    let mut ibo = 0;
    gl::GenBuffers(1, &mut ibo);
    gl::BindBuffer(gl::ELEMENT_ARRAY_BUFFER, ibo);
    // Fill the IBO with the indices
    gl::BufferData(gl::ELEMENT_ARRAY_BUFFER, byte_size_of_array(&mesh.indices), pointer_to_array(&mesh.indices), gl::STATIC_DRAW);
    
    return array;
}