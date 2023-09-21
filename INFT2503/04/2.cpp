#include <iostream>
#include <vector>
#include <gtkmm.h>

using namespace std;

class Window : public Gtk::Window {
public:
    Gtk::VBox vbox;
    Gtk::Entry entry_a, entry_b;
    Gtk::Button button;
    Gtk::Label label, entry_label_a, entry_label_b;

    Window() {
        button.set_label("Full name");
        entry_label_a.set_text("Firstname");
        entry_label_b.set_text("Lastname");

        vbox.pack_start(entry_label_a);
        vbox.pack_start(entry_a);
        vbox.pack_start(entry_label_b);
        vbox.pack_start(entry_b);
        vbox.pack_start(button);
        vbox.pack_start(label);

        add(vbox);
        show_all();

        button.set_sensitive(false);

        entry_a.signal_changed().connect([this]() {
            button.set_sensitive((entry_a.get_text().size() != 0 && entry_b.get_text().size() != 0));
        });

        entry_b.signal_changed().connect([this]() {
            button.set_sensitive(entry_a.get_text().size() != 0 && entry_b.get_text().size() != 0);
        });

        button.signal_clicked().connect([this]() {
            label.set_text(entry_a.get_text() + " " + entry_b.get_text());
        });
    }
};

int main() 
{
    Gtk::Main gtk_main;
    Window window;
    gtk_main.run(window);
}
