use glm::{vec3, Vec3};
use std::f32::consts::PI;
use std::f32::consts::FRAC_PI_2;

pub struct Camera {
    position: Vec3,
    yaw: f32,
    pitch: f32,
    forward: Vec3,
    up: Vec3,
    speed: f32,
}

impl Camera {
    pub fn new() -> Self {
        Camera {
            position: vec3(0.0, 0.0, 0.0),
            yaw: 0.0,
            pitch: 0.0,
            forward: vec3(0.0, 0.0, -1.0),
            up: vec3(0.0, 1.0, 0.0),
            speed: 1.5,
        }
    }

    fn calc_right(&mut self) -> Vec3 {
        // Cross product of the forward and up vectors
        return glm::cross(&self.forward, &self.up);
    }

    fn update_forward(&mut self) {
        self.forward = vec3(
            self.pitch.cos() * self.yaw.cos(),
            self.pitch.sin(),
            -self.pitch.cos() * self.yaw.cos()
        );
    }

    pub fn to_view_matrix(&self, perspective: glm::Mat4) -> glm::Mat4 {
        let look_at: glm::Mat4 = glm::look_at(
            &self.position, 
            &(self.position + self.forward), 
            &self.up
        );

        let translate: glm::Mat4 = glm::translation(&glm::vec3(0.0, 0.0, -2.0));
        let projection: glm::Mat4 = look_at * perspective * translate;
        return projection;
    }

    pub fn move_x(&mut self, delta: f32) {
        let right = self.calc_right();
        self.position += right * delta * self.speed;
    }

    pub fn move_y(&mut self, delta: f32) {
        self.position += self.up * delta * self.speed;
    }

    pub fn move_z(&mut self, delta: f32) {
        self.position += self.forward * delta * self.speed;
    }

    pub fn rot_yaw(&mut self, delta: f32) {
        self.yaw += delta;
        self.update_forward();
    }

    pub fn rot_pitch(&mut self, delta: f32) {
        self.pitch += delta;
        self.update_forward();
    }
}