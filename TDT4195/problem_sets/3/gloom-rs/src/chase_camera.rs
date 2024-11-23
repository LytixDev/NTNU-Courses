extern crate nalgebra_glm as glm;

pub struct ChaseCamera {
    pub position: glm::Vec3,

    pub target_position: glm::Vec3,
    pub target_direction: glm::Vec3,
    
    pub radius: f32,
    pub height: f32,
}

impl ChaseCamera {
    pub fn new(radius: f32, height: f32) -> Self {
        let zero = glm::vec3(0.0, 0.0, 0.0);
        ChaseCamera {
            position: zero,
            target_direction: zero,
            target_position: zero,
            radius,
            height,
        }
    }

    pub fn set_target(&mut self, target: glm::Vec3, target_direction: glm::Vec3) {
        self.target_position = target;
        self.target_direction = target_direction;
    }

    pub fn update(&mut self) -> glm::Mat4 {
        let camera_offset = -glm::normalize(&self.target_direction) * self.radius;
        let desired_camera_position = self.target_position + camera_offset + glm::vec3(0.0, self.height, 0.0);
        // Smoothing factor based on the distance between the camera and the desired camera position
        let smooth = glm::length(&(self.position - desired_camera_position)) * 0.005;
        self.position = glm::lerp(&self.position, &desired_camera_position, smooth);
        // View matrix
        return glm::look_at(&self.position, &self.target_position, &glm::vec3(0.0, 1.0, 0.0));
    }
}