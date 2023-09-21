#include <string>

#include "commodity.hpp"


// constructor
Commodity::Commodity(const std::string &name_, int id_, double price_) : name(name_), id(id_), price(price_) {}

const std::string &Commodity::get_name() const {
    return name;
}

int Commodity::get_id() const {
    return id;
}

double Commodity::get_price() const {
    return price;
}

double Commodity::get_price(double quantity) const {
    return price * quantity;
}

void Commodity::set_price(double price_) {
    this->price = price_;
}

double Commodity::get_price_with_sales_tax(double quantity) const {
    double price_without_tax = get_price(quantity);
    return price_without_tax * (SALES_TAX + 1);
}
