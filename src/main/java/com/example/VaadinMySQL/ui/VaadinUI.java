package com.example.VaadinMySQL.ui;

import com.example.VaadinMySQL.model.Customer;
import com.example.VaadinMySQL.service.CustomerService;
import com.example.VaadinMySQL.service.ExcelService;
import com.example.VaadinMySQL.service.PDFService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringUI
public class VaadinUI extends UI {

    @Autowired
    private CustomerService customerService;

    //Grid listing the customers
    private Grid<Customer> customerGrid = new Grid<>(Customer.class);
    //Text field that works as a filter
    private TextField filterField = new TextField();
    //Side form to add, edit and delete customers
    private CustomerForm form = new CustomerForm(this);
    //Toolbar on top of grid to filter and add customers
    private HorizontalLayout toolbar;

    @Override
    protected void init(VaadinRequest request) {
        Layout layout = buildUI();
        setContent(layout);
    }

    private Layout buildUI(){
        //Create the general vertical layout of the UI
        final VerticalLayout layout = new VerticalLayout();

        //Add the components to the layout
        layout.addComponents(buildTopToolbar(), buildMainLayout(), buildBottomToolBar());

        // fetch list of Customers from service and assign it to Grid
        this.updateGrid();

        form.setVisible(false);

        return layout;
    }

    private HorizontalLayout buildMainLayout(){
        //Build the grid
        buildGrid();

        //Add the form and grid to the layout
        HorizontalLayout main = new HorizontalLayout(customerGrid, form);

        //Configure the grid and layout size
        main.setSizeFull();
        customerGrid.setSizeFull();
        main.setExpandRatio(customerGrid, 1);

        return main;
    }

    /**
     * Configures the Grid for the Customers table
     */
    private Grid buildGrid(){
        //Set the grids columns and add a listener for when selecting a customer
        updateGrid();
        customerGrid.setColumns("firstName", "lastName", "email", "joinDate");
        customerGrid.asSingleSelect().addValueChangeListener(event -> selectRow());

        return customerGrid;
    }

    /**
     * Builds a toolbar that contains:
     * Text field for filtering
     * Button for clearing the filter
     * Button for adding a new element to the grid
     * @return The built toolbar as an horizontal layout
     */
    private HorizontalLayout buildTopToolbar(){
        //Set the text field as filter
        filterField.setPlaceholder("Filter by name...");
        filterField.addValueChangeListener(e -> updateGrid());
        filterField.setValueChangeMode(ValueChangeMode.LAZY);

        //Add clear button next to the text field
        Button clearFilterButton = new Button(VaadinIcons.CLOSE);
        clearFilterButton.setDescription("Clear the current filter");
        clearFilterButton.addClickListener(e -> clearFilter());

        // Add add customer button
        Button addCustomerButton = new Button("New customer");
        addCustomerButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        addCustomerButton.addClickListener(event -> {
            customerGrid.asSingleSelect().clear();
            form.setCustomer(new Customer());
            form.show();
        });

        // Create a CSS Layout
        CssLayout filteringLayout = new CssLayout();
        filteringLayout.addComponents(filterField, clearFilterButton, addCustomerButton);
        filteringLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        // Make the toolbar
        this.toolbar = new HorizontalLayout(filteringLayout);

        return this.toolbar;
    }

    private HorizontalLayout buildBottomToolBar(){
        // Add a print pdf button
        Button pdfButton = new Button("PDF");
        pdfButton.setIcon(VaadinIcons.FILE_TEXT_O);
        pdfButton.setStyleName("danger");
        pdfButton.addClickListener(event -> downloadReport("customers", "pdf"));

        // Add a generate excel file button
        Button excelButton = new Button("Excel");
        excelButton.setIcon(VaadinIcons.FILE_TABLE);
        excelButton.setStyleName("friendly");
        excelButton.addClickListener(event -> downloadReport("customers", "excel"));

        // Make the toolbar
        HorizontalLayout bottomToolbar = new HorizontalLayout(pdfButton, excelButton);

        return bottomToolbar;
    }

    /**
     * Sets the customer property from the CustomerForm object to the one selected in the grid
     */
    private void selectRow() {
        if (customerGrid.asSingleSelect().isEmpty()) {
            form.setVisible(false);
        } else {
            form.setCustomer(customerGrid.asSingleSelect().getValue());
            form.show();
        }
    }

    public void updateGrid() {
        List<Customer> customers = customerService.findAll(this.filterField.getValue());
        customerGrid.setItems(customers);
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setFilterFieldText(String text){
        this.filterField.setValue(text);
    }

    public void clearFilter(){
        this.filterField.clear();
    }

    /**
     * Prints a red error message in the center of the screen.
     * Must be closed by the user.
     * @param caption the title of the message
     * @param description the contents of the message box
     */
    public void showError(String caption, String description){
        new Notification(caption,
                description,
                Notification.Type.ERROR_MESSAGE, true)
                .show(Page.getCurrent());
    }

    /**
     * Prints a message in the bottom left corner of the screen.
     * Lasts for 3 seconds.
     * @param caption the title of the message
     * @param description the contents of the message box
     */
    public void showMessage(String caption, String description){
        new Notification(caption,
                description,
                Notification.Type.TRAY_NOTIFICATION, true)
                .show(Page.getCurrent());
    }

    private void downloadReport(String name, String fileType){
        String filepath;
        //Evaluate if PDF or Excel and generate accordingly
        if(fileType.compareTo("pdf") == 0){
            generatePDF(name);
             filepath = "reports/pdf/"+name+".pdf";
        } else{
            generateExcel(name);
            filepath = "reports/excel/"+name+".xlsx";
        }
        //Download it
        downloadFile(filepath);
    }

    private void downloadFile(String path){
        byte[] fileToPrint = new byte[0];
        try {
            fileToPrint = fileToByteArray(path);
            // toPrint has the data containing the file
            downloadExportFile(fileToPrint, path);
        } catch (IOException e) {
            e.printStackTrace();
            showError("File not found","Couldn't find file to download.");
        }
    }

    /**
     * Generates an excel file with the given name on the reports/excel directory
     * @param name
     */
    private void generateExcel(String name){
        String filePath = "reports/excel/"+name+".xlsx"; //Set the destination for the file
        ExcelService<Customer> excelService = new ExcelService<>(); //Create a excel service for the Customer class
        String[] headerArray = {"First Name", "Last Name", "Email", "Join Date"}; //Define the table headers
        List<Customer> customers =  customerService.findAll(); //Get the data from DB
        try {
            //Generate the excel file
            excelService.createExcelFile(name, headerArray, customers);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Excel Error","Couldn't generate excel report. Please try again. " +
                    "If problem persists, please contact support.");
        }
    }

    /**
     * Generates a PDF file with the given name
     * @param name
     */
    private void generatePDF(String name){
        String destination = "reports/pdf/"+name+".pdf"; //Define the file destination path
        PDFService<Customer> pdfService = new PDFService<>(); //Define a PDFService that uses the Customer class
        String[] headerArray = {"First Name", "Last Name", "Email", "Join Date"}; //Define the arrays to print
        float[] headerWidth = {1, 1, 1, 1}; //Define their width
        List<Customer> customers =  customerService.findAll(); //Get the data from DB
        try {
            //Print the PDF and store it in the reports folder
            pdfService.printTablePDF(destination, headerArray, headerWidth, customers);
        } catch (IOException e) {
            e.printStackTrace();
            showError("PDF Error","Couldn't generate pdf report. Please try again. " +
                    "If problem persists, please contact support.");
        }
    }

    /**
     * Converts a file to a byte array.
     * @param filePath the location of the file
     * @return the file as a byte array
     * @throws IOException
     */
    private byte[] fileToByteArray(String filePath) throws IOException {
        //Take the generated PDF file and return it as a byte array
        Path path = Paths.get(filePath);
        byte[] pdfBytes = Files.readAllBytes(path);
        return pdfBytes;
    }
    /**
     * Opens a dialog to download a file.
     * @param toDownload the file as a byte array
     * @param path the location of the file
     */
    public void downloadExportFile(byte[] toDownload, String path) {
        StreamResource.StreamSource source = new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return new ByteArrayInputStream(toDownload);
            }
        };
        // by default getStream always returns new DownloadStream. Which is weird because it makes setting stream parameters impossible.
        // It seems to be working before in earlier versions of Vaadin. We'll override it.
        StreamResource resource = new StreamResource(source, path) {
            DownloadStream downloadStream;
            @Override
            public DownloadStream getStream() {
                if (downloadStream==null)
                    downloadStream = super.getStream();
                return downloadStream;
            }
        };
        resource.getStream().setParameter("Content-Disposition","attachment;filename=\""+path+"\""); // or else browser will try to open resource instead of download it
        resource.getStream().setParameter("Content-Type","application/octet-stream");
        resource.getStream().setCacheTime(0);
        ResourceReference ref = new ResourceReference(resource, this, "download");
        this.setResource("download", resource); // now it's available for download
        Page.getCurrent().open(ref.getURL(), null);
    }

}
