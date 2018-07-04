package com.example.VaadinMySQL.ui;

import com.example.VaadinMySQL.model.Customer;
import com.example.VaadinMySQL.service.CustomerService;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

public class CustomerForm extends com.vaadin.ui.FormLayout{

    private Binder<Customer> binder = new Binder<>(Customer.class);

    // Vaadin components to be used
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField email = new TextField("Email");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    // References to the currently edited customer
    private Customer customer;
    private VaadinUI myUI;

    public CustomerForm(VaadinUI myUI) {
        this.myUI = myUI;

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        addComponents(firstName, lastName, email, buttons);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());

        binder.bindInstanceFields(this);
    }

    public void show(){
        // Show delete button for only customers already in the database
        this.delete.setVisible(customer.isPersisted());
        //Switch between 'Save' & 'Update' depending if the Customer is already in the database
        this.switchSaveButtonCaption(customer.isPersisted());

        setVisible(true);
        firstName.selectAll();
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
        binder.setBean(customer);
    }

    public Customer getCustomer(){
        return this.customer;
    }

    private void delete() {
        try {
            myUI.getCustomerService().delete(customer);
        } catch (DataIntegrityViolationException e) {
            this.myUI.showError("Cannot Delete",
                    "Can't delete customers when they are referenced in other tables.");
        }
        myUI.clearFilter();
        myUI.updateGrid();
        setVisible(false);
    }

    private void save() {
        myUI.getCustomerService().save(customer);
        myUI.showMessage("Customer added","Added new customer: "
                                 + customer.getFirstName() + " " + customer.getLastName());
        myUI.clearFilter();
        myUI.updateGrid();
        setVisible(false);
    }

    public void switchSaveButtonCaption(Boolean persisted){
        if(persisted)
            this.save.setCaption("Update");
        else
            this.save.setCaption("Save");
    }
}

