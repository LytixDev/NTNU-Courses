#include "circle.hpp"

// ==> Implementasjon av klassen Circle

Circle::Circle(double radius_) : radius(radius_) {}

int Circle::get_area() const {
  return pi * radius * radius;
}

double Circle::get_circumference() const {
  double circumference = 2.0 * pi * radius;
  return circumference;
}
