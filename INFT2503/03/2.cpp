#include <iostream>
#include "circle.hpp"

using namespace std;

int main(void) {
  Circle circle(5);

  int area = circle.get_area();
  cout << "Arealet er lik " << area << endl;

  double circumference = circle.get_circumference();
  cout << "Omkretsen er lik " << circumference << endl;
}
