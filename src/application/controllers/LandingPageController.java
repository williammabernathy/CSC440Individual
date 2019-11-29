package application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LandingPageController
{
    private static Connection connection;

    // general/main pane elements
    @FXML private TextField searchTextField;
    @FXML private Label displaySelectedCustomer;
    @FXML private Button mainAccountButton;
    @FXML private Button mainCustomersButton;
    @FXML private Button mainLoanServices;
    @FXML private Button mainMoneyExchangeButton;
    @FXML private Button loggedUserActorButton;
    @FXML private HBox searchBox;

    // customer search pane and children
    @FXML private Pane selectCustomerPane;
    @FXML private Pane createNewCustomerPane;
    @FXML private Pane createNewAccountPane;
    @FXML private Pane customerListPane;
    @FXML private ListView customerListView;
    @FXML private Button mofifyButton;

    // create customer fields
    @FXML private Button CancelButtonClicked;
    @FXML private Button submitNewCustomerButton;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField phoneTextField;
    @FXML private DatePicker datePickerField;
    @FXML private TextField addressTextField;
    @FXML private TextField cityTextField;
    @FXML private TextField stateTextField;
    @FXML private TextField zipcodeTextField;

    // account pane and children
    @FXML private Pane accountPane;
    @FXML private ListView allAccountsListView;
    @FXML private Button selectAccountButton;
    @FXML private Button submitNewAccountButton;
    @FXML private Button cancelNewAccountButton;
    @FXML private TextField accountCustID;
    @FXML private ComboBox accountTypeField;
    @FXML private TextField accountHolderName;
    @FXML private TextField accountBalanceField;

    //modify customer pane and children
    @FXML private Pane modifyCustomerPane;
    @FXML private TextField modifiedFirstNameTextField;
    @FXML private TextField modifiedLastNameTextField;
    @FXML private TextField modifiedPhoneTextField;
    @FXML private DatePicker modifedDatePickerField;
    @FXML private TextField modifiedAddressTextField;
    @FXML private TextField modifiedCityTextField;
    @FXML private TextField mofifiedStateTextField;
    @FXML private TextField modifiedZipcodeTextField;
    @FXML private Button submitModifiedCustomerButtonClicked;
    @FXML private Button cancelModifiedCustomerButtonClicked;

    //Money Exchange
    @FXML private TextArea moneyExchangeTextArea;
    @FXML private Pane moneyExchangePane;

    //Withdraw Money Pane children
    @FXML private TextField withdrawAmountTextField;
    @FXML private ComboBox withdrawAccountTypeComboBox;
    @FXML private TextArea moneyExchangeTextAreaWithdraw;
    @FXML private Button submitWithdrawButton;

    //Deposit Money Pane children
    @FXML private TextField depositAmountTextField;
    @FXML private ComboBox depositAccountTypeComboBox;
    @FXML private TextArea moneyExchangeTextAreaDeposit;
    @FXML private Button submitDepositButton;

    //Transfer Money Pane children
    @FXML private TextField transferAmountTextField;
    @FXML private TextField fromTransferMoneyTextField;
    @FXML private TextField toTransferMoneyTextField;
    @FXML private Button submitTransferButton;

    //Loan Services Pane and children
    @FXML private Pane loanServicesPane;
    @FXML private ListView loanCustomerAccountListView;
    @FXML private TextField loanCustomerIDTextField;
    @FXML private TextField loanAccountHolderTextField;
    @FXML private TextField loanAmountTextField;
    @FXML private Button loanSearchCustomerButton;
    @FXML private Button loanSubmitButton;
    @FXML private Button loanCancelButton;

    // global variables
    private static String[][] allCustomers;
    private static String[][] searchedCustomers;
    private static String[][] allAccounts;
    int selectedCust;

    public LandingPageController()
    {

    }

    //all elements to be called when scene is first loaded should be here
    public void initialize()
    {
        connection = setupConnection();

        mofifyButton.setDisable(true);
        mainAccountButton.setDisable(true);
        mainLoanServices.setDisable(true);
        mainMoneyExchangeButton.setDisable(true);
        displaySelectedCustomer.setText("Selected Customer: ");

        //fill combobox in account creation pane with values
        accountTypeField.getItems().addAll("Checking", "Savings");

        //disable the sidebar buttons until customer is selected
        mainAccountButton.setDisable(true);
        mainLoanServices.setDisable(true);
        mainMoneyExchangeButton.setDisable(true);

        //set the customer listview selection type to only allow a single customer to be selected
        customerListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //update logout button with what user is currently logged in
        loggedUserActorButton.setText("Log out of: "+ LoginController.getUsername());

        //get all customers from database
        allCustomers = getAllCustomers();

        //fill the listview with all customers
        customerListView.getItems().addAll(allCustomers);
    }

    // refresh all contents of the customer listview
    public void refreshCustomerListView()
    {
        selectedCust = -1;
        mofifyButton.setDisable(true);
        mainAccountButton.setDisable(true);
        mainLoanServices.setDisable(true);
        mainMoneyExchangeButton.setDisable(true);

        displaySelectedCustomer.setText("Selected Customer: ");

        //clear the current items in the listview
        customerListView.getItems().clear();

        //set the customer listview selection type to only allow a single customer to be selected
        customerListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //get all customers from database
        allCustomers = getAllCustomers();

        //fill the listview with all customers
        customerListView.getItems().addAll(allCustomers);
    }

    // refresh all contents of the customer listview
    public void refreshAccountListView()
    {
        //clear the current items in the listview
        allAccountsListView.getItems().clear();

        //get all accounts related to the selected customers
        allAccounts = getAllAccounts(allCustomers[selectedCust][0]);

        //fill listview with accounts under that customer
        allAccountsListView.getItems().addAll(allAccounts);
    }

    /*
     *
     * General/Main Pane Functions
     *
     */
    //log out button
    public void loggedUserActorClick(ActionEvent event)
    {
        //get the current stage
        Stage stage = (Stage) loggedUserActorButton.getScene().getWindow();

        //close it to return to log in screen
        stage.close();
    }

    //account sidebar button
    public void accountButtonClicked(MouseEvent mouseEvent)
    {
        searchBox.setVisible(false);
        searchTextField.setPromptText("Search for an account with account number or customer ID");
        displaySelectedView(accountPane);

        accountCustID.setText(allCustomers[selectedCust][0]);
        accountHolderName.setText(allCustomers[selectedCust][2] +", "+allCustomers[selectedCust][1]);
    }

    //customer side bar button
    public void customersButtonClicked(MouseEvent mouseEvent)
    {
        displaySelectedView(customerListPane);
        searchTextField.setPromptText("Search for an customer with name or customer ID");
        searchBox.setVisible(true);
    }

    public void searchTextButtonClicked(ActionEvent mouseEvent)
    {
        String searchQuery = searchTextField.getText();

        searchedCustomers = searchCustomer(searchQuery);

        //clear the current items in the listview
        customerListView.getItems().clear();

        //set the customer listview selection type to only allow a single customer to be selected
        customerListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //fill the listview with all customers
        customerListView.getItems().addAll(searchedCustomers);
    }

    /*
     *
     * Customer List Pane (Home)
     *
     */
    // select customer button
    public void selectButtonClicked(ActionEvent event)
    {
        allAccountsListView.getItems().clear();

        selectedCust = customerListView.getSelectionModel().getSelectedIndex();

        //if nothing is selected
        if(selectedCust == -1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer.", ButtonType.OK);
            alert.showAndWait();
        }
        //if a customer IS selected
        else
        {
            //get the selected Customer object
            selectedCust = customerListView.getSelectionModel().getSelectedIndex();

            //set the text to display what customer we are working with
            displaySelectedCustomer.setText("Selected Customer: " + allCustomers[selectedCust][1]);

            //get all accounts related to the selected customers
            allAccounts = getAllAccounts(allCustomers[selectedCust][0]);

            //fill listview with accounts under that customer
            allAccountsListView.getItems().addAll(allAccounts);

            mainLoanServices.setDisable(false);
            mainMoneyExchangeButton.setDisable(false);
            mainAccountButton.setDisable(false);
            mofifyButton.setDisable(false);
        }
    }

    /*
    *
    * Modify Customer Pane
    *
    */
    // modify customer information from customer tab
    public void modifyButtonClicked(MouseEvent mouseEvent) {
        searchBox.setVisible(false);
        displaySelectedView(modifyCustomerPane);

        //convert the date to localdate
        //Date conv = allCustomers[selectedCust][8];
        //Instant instant = Instant.ofEpochMilli(conv.getTime());
        //LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        //LocalDate localDate = localDateTime.toLocalDate();

        //set textfields with current customer information
        modifiedFirstNameTextField.setText(allCustomers[selectedCust][1]);
        modifiedLastNameTextField.setText(allCustomers[selectedCust][2]);
        modifiedPhoneTextField.setText(allCustomers[selectedCust][7]);
        //modifedDatePickerField.setValue(localDate);
        modifiedAddressTextField.setText(allCustomers[selectedCust][3]);
        modifiedCityTextField.setText(allCustomers[selectedCust][4]);
        mofifiedStateTextField.setText(allCustomers[selectedCust][5]);
        modifiedZipcodeTextField.setText(allCustomers[selectedCust][6]);
    }

    public void submitModifiedCustomerButtonClicked(ActionEvent actionEvent)
    {
        // check that all input fields have a value
        if(modifiedFirstNameTextField.getText() == null || modifiedLastNameTextField.getText() == null || modifiedPhoneTextField.getText() == null || modifedDatePickerField.getValue() == null ||
                modifiedAddressTextField.getText() == null || modifiedCityTextField.getText() == null || mofifiedStateTextField.getText() == null || modifiedZipcodeTextField.getText() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be entered!", ButtonType.OK);
            alert.showAndWait();
        }
        else
        {
            // if all inputs are present, get the values to pass
            String custID = allCustomers[selectedCust][0];
            String firstName = modifiedFirstNameTextField.getText();
            String lastName = modifiedLastNameTextField.getText();
            String phoneNum = modifiedPhoneTextField.getText();
            LocalDate birthday = modifedDatePickerField.getValue();
            String address = modifiedAddressTextField.getText();
            String city = modifiedCityTextField.getText();
            String state = mofifiedStateTextField.getText();
            String zip = modifiedZipcodeTextField.getText();

            // insert new customer into database
            int check = modifyCustomer(custID, firstName, lastName, phoneNum, birthday, address, city, state, zip);

            // if check > 1, insert was successful
            if(check > 0)
            {
                // display success notification
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer information has been updated successfully!", ButtonType.OK);
                alert.showAndWait();

                // clear all fields
                modifiedFirstNameTextField.clear();
                modifiedLastNameTextField.clear();
                modifiedPhoneTextField.clear();
                modifedDatePickerField.setValue(null);
                modifiedAddressTextField.clear();
                modifiedCityTextField.clear();
                mofifiedStateTextField.clear();
                modifiedZipcodeTextField.clear();

                //refresh list view
                refreshCustomerListView();

                //return to customer list
                displaySelectedView(customerListPane);
                searchBox.setVisible(true);
            }
            // something went wrong with insert
            else
            {
                // display error message
                Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong while trying to update customer information.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    public void cancelModifiedCustomerButtonClicked(ActionEvent actionEvent)
    {
        // clear all fields
        modifiedFirstNameTextField.clear();
        modifiedLastNameTextField.clear();
        modifiedPhoneTextField.clear();
        modifedDatePickerField.setValue(null);
        modifiedAddressTextField.clear();
        modifiedCityTextField.clear();
        mofifiedStateTextField.clear();
        modifiedZipcodeTextField.clear();

        //refresh list view
        refreshCustomerListView();

        //return to customer list
        displaySelectedView(customerListPane);
        searchBox.setVisible(true);
    }

    /*
    *
    * Create Customer Pane
    *
    */
    // create a new customer from the customer tab
    public void createNewCustomerButtonClicked(ActionEvent mouseEvent) {
        searchBox.setVisible(false);
        displaySelectedView(createNewCustomerPane);
    }

    // submit a newly created customer from the new/create customer tab
    public void submitNewCustomerButtonClicked(ActionEvent event)
    {
        // check that all input fields have a value
        if(firstNameTextField.getText() == null || lastNameTextField.getText() == null || phoneTextField.getText() == null || datePickerField.getValue() == null ||
                addressTextField.getText() == null || cityTextField.getText() == null || stateTextField.getText() == null || zipcodeTextField.getText() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be entered!", ButtonType.OK);
            alert.showAndWait();
        }
        else
        {
            // if all inputs are present, get the values to pass
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String phoneNum = phoneTextField.getText();
            LocalDate birthday = datePickerField.getValue();
            String address = addressTextField.getText();
            String city = cityTextField.getText();
            String state = stateTextField.getText();
            String zip = zipcodeTextField.getText();

            // insert new customer into database
            int check = createNewCustomer(firstName, lastName, phoneNum, birthday, address, city, state, zip);

            // if check > 1, insert was successful
            if(check > 0)
            {
                // display success notification
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "New customer created!", ButtonType.OK);
                alert.showAndWait();

                // clear all fields
                firstNameTextField.clear();
                lastNameTextField.clear();
                phoneTextField.clear();
                datePickerField.setValue(null);
                addressTextField.clear();
                cityTextField.clear();
                stateTextField.clear();
                zipcodeTextField.clear();

                //refresh list view
                refreshCustomerListView();

                //return to customer list
                displaySelectedView(customerListPane);
                searchBox.setVisible(true);
            }
            // something went wrong with insert
            else
            {
                // display error message
                Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong while trying to insert a new customer", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    // cancel creating new customer
    public void cancelNewCustomerButtonClicked(ActionEvent event)
    {
        // clear all fields
        firstNameTextField.clear();
        lastNameTextField.clear();
        phoneTextField.clear();
        datePickerField.setValue(null);
        addressTextField.clear();
        cityTextField.clear();
        stateTextField.clear();
        zipcodeTextField.clear();

        //refresh list view
        refreshCustomerListView();

        //return to customer list
        displaySelectedView(customerListPane);
        searchBox.setVisible(true);
    }

    /*
    *
    * Account List Pane
    *
    */

    public void createNewAccountButtonClicked(ActionEvent mouseEvent)
    {
        searchBox.setVisible(false);
        displaySelectedView(createNewAccountPane);
    }

    /*
    *
    * Creating New Account
    *
    */
    //submit the new account
    public void submitNewAccount(ActionEvent mouseEvent)
    {
        //check that balance is a double/number
        try
        {
            Double.parseDouble(accountBalanceField.getText());
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Balance must be a number!", ButtonType.OK);
            alert.showAndWait();

        }
        //check that all fields have data
        if(accountBalanceField.getText() == null || accountTypeField.getValue() == null)
        {
            // display error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be entered!", ButtonType.OK);
            alert.showAndWait();
        }
        else
        {
            //get entered values
            String customerID = accountCustID.getText();
            String accountType = "";
            if(accountTypeField.getValue() == "Savings")
            {
                accountType = "S";
            }
            else if(accountTypeField.getValue() == "Checking")
            {
                accountType = "C";
            }

            //get current date
            LocalDate creationDate = java.time.LocalDate.now();
            String amount = accountBalanceField.getText();

            //query database with entered information
            int check = createNewAccount(customerID, accountType, creationDate, amount);

            // if check > 1, insert was successful
            if(check > 0)
            {
                // display success notification
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "New Account created!", ButtonType.OK);
                alert.showAndWait();

                // clear all fields
                accountTypeField.valueProperty().set(null);
                accountBalanceField.clear();

                //refresh list view
                refreshAccountListView();

                //return to customer list
                displaySelectedView(accountPane);
            }
            // something went wrong with insert
            else
            {
                // display error message
                Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong while trying to create a new account!", ButtonType.OK);
                alert.showAndWait();
            }

        }
    }

    //cancel creating the account
    public void cancelNewAccount(ActionEvent mouseEvent)
    {
        accountTypeField.valueProperty().set(null);
        accountBalanceField.clear();

        //refresh list view
        refreshAccountListView();

        //return to customer list
        displaySelectedView(accountPane);
    }

    /*
    *
    * Loan Services Pane
    * Zach
    */
    public void loanServicesButtonClicked(ActionEvent mouseEvent) {
        searchBox.setVisible(false);
        displaySelectedView(loanServicesPane);
        popCustIDfield();
        popCustName();
    }
    public void popCustIDfield(){
        loanCustomerIDTextField.setText(allCustomers[selectedCust][0]);
    }
    public void popCustName(){
        loanAccountHolderTextField.setText(allCustomers[selectedCust][2] + ", " + allCustomers[selectedCust][1]);
    }
    public void newLoanSumbit(ActionEvent actionEvent){
        String custID = loanCustomerIDTextField.getText();
        String loanAmount = loanAmountTextField.getText();
        if(validateCustID(custID) && validateAmount(loanAmount)){
            enterLoanToDB(custID, loanAmount);
        }
    }

    private void enterLoanToDB(String customerID, String amount) {
        //get current date
        LocalDate creationDate = java.time.LocalDate.now();
        amount = "-" + amount;

        //query database with entered information
        int check = createNewAccount(customerID, "L", creationDate, amount);
        if(successfulNewAccount(check)){
            refreshAccountListView();
        }
    }

    private boolean validateCustID(String custID) {
        try {
            Double.parseDouble(custID);
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "ID must be a number!", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }
    public void loanCancel(ActionEvent actionEvent){
        loanAmountTextField.clear();
    }


    /*
     *
     * Money Services Pane
     *
     */

    public void moneyExchangeButtonClicked(ActionEvent actionEvent)
    {
        searchBox.setVisible(false);
        displaySelectedView(moneyExchangePane);
        refreshDepositCB();
        refreshWithdrawCB();
    }

    //// withdraw

    public void updateWithdrawTextArea(Account selectedAccount)
    {
        moneyExchangeTextAreaWithdraw.setText(String.format("Customer %s has a total\n" + "of $%.2f in his %s account", allCustomers[selectedCust][1], selectedAccount.getAccAmount(), selectedAccount.getAccType()));
    }

    public void withdrawComboBoxChange()
    {
        if(withdrawAccountTypeComboBox.getValue() != null)
        {
            updateWithdrawTextArea((Account) withdrawAccountTypeComboBox.getValue());
        }
    }

    private void refreshWithdrawCB()
    {
        withdrawAccountTypeComboBox.getItems().clear();
        withdrawAccountTypeComboBox.getItems().addAll(allAccounts);
        moneyExchangeTextAreaWithdraw.setText("No Account Selected");
    }

    public void submitWithdrawButtonClicked(ActionEvent actionEvent)
    {
        if(validateAmount(withdrawAmountTextField.getText()) && withdrawAccountTypeComboBox.getValue() != null)
        {
            LocalDate creationDate = java.time.LocalDate.now();
            Account selectedAccount = (Account) withdrawAccountTypeComboBox.getValue();
            int check = DepositWithdraw.createNewDWEntry(selectedAccount.getAccID(), creationDate, withdrawAmountTextField.getText(), 'w');

            if(successfulWithdraw(check))
            {
                refreshAccountListView();
                refreshWithdrawCB();
                refreshDepositCB();
            }
        }
    }

    ////deposit
    public void updateDepositTextArea(Account selectedAccount, Customer selectedCustomer)
    {
        moneyExchangeTextAreaDeposit.setText(String.format("Customer %s has a total\n" + "of $%.2f in his %s account",selectedCustomer.getFname(), selectedAccount.getAccAmount(), selectedAccount.getAccType()));
    }

    public void depositComboBoxChange()
    {
        if(depositAccountTypeComboBox.getValue() != null)
        {
            updateDepositTextArea((Account) depositAccountTypeComboBox.getValue(), selectedCust);
        }
    }

    private void refreshDepositCB()
    {
        depositAccountTypeComboBox.getItems().clear();
        depositAccountTypeComboBox.getItems().addAll(allAccounts);
        moneyExchangeTextAreaDeposit.setText("No Account Selected");
    }

    public void submitDepositButtonClicked(ActionEvent actionEvent)
    {
        if(validateAmount(depositAmountTextField.getText()) && depositAccountTypeComboBox.getValue() != null)
        {
            LocalDate creationDate = java.time.LocalDate.now();
            Account selectedAccount = (Account) depositAccountTypeComboBox.getValue();
            int check = createNewDWEntry(selectedAccount.getAccID(), creationDate, depositAmountTextField.getText(), 'd');

            if(successfulDeposit(check))
            {
                refreshAccountListView();
                refreshDepositCB();
                refreshWithdrawCB();
            }
        }
    }

    ////transfer
    public void submitTransferButtonClicked(ActionEvent actionEvent)
    {
        String amount = transferAmountTextField.getText();
        validateAmount(amount);
        String from = fromTransferMoneyTextField.getText();
        String to = toTransferMoneyTextField.getText();

        if(!from.equals(to))
        {
            if(validateAccountIDformat(from) &&validateAccountIDformat(to))
            {
                LocalDate creationDate = java.time.LocalDate.now();
                int check = newTransfer(from, to, creationDate, amount);

                if (successfulTransfer(check))
                {
                    refreshAccountListView();
                    refreshDepositCB();
                    refreshWithdrawCB();
                    clearTransferFields();
                }
            }
        }
        else
            {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "To and From account can not be the same!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void clearTransferFields()
    {
        transferAmountTextField.clear();
        fromTransferMoneyTextField.clear();
        toTransferMoneyTextField.clear();
    }


    /*
     *
     * Other
     *
     */

    public void displaySelectedView(Pane selectedView)
    {
        Pane[] arrayOfViews = {customerListPane, createNewAccountPane, modifyCustomerPane, createNewCustomerPane, selectCustomerPane, accountPane, moneyExchangePane, loanServicesPane};

        for (Pane p : arrayOfViews)
        {
            if (p != selectedView)
            {
                p.setVisible(false);
                accountBalanceField.clear();
                //return;
            }
            else
                {
                p.setVisible(true);
            }
        }
    }

    private boolean validateAmount(String amount)
    {
        try
        {
            Double.parseDouble(amount);
        }
        catch (NumberFormatException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Amount must be a number!", ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        if (Double.parseDouble(amount) < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Amount must be a greater than 0!", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean validateAccountIDformat(String id)
    {
        try
        {
            Integer.parseInt(id);

            if (Integer.parseInt(id) < 0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "ID must be a greater than 0!", ButtonType.OK);
                alert.showAndWait();
                return false;
            }
        }
        catch (NumberFormatException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "ID must be a Integer!", ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private boolean successfulDeposit(int check)
    {
        if (check > 0)
        {
            // display success notification
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deposit Successful!", ButtonType.OK);
            alert.showAndWait();
            loanAmountTextField.clear();
            return true;
        }
        return false;
    }

    private boolean successfulWithdraw(int check)
    {
        if (check > 0)
        {
            // display success notification
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Withdraw Successful!", ButtonType.OK);
            alert.showAndWait();
            loanAmountTextField.clear();
            return true;
        }
        return false;
    }

    private boolean successfulTransfer(int check)
    {
        if (check > 0){
            // display success notification
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Transfer Successful!", ButtonType.OK);
            alert.showAndWait();
            loanAmountTextField.clear();
            return true;
        }
        return false;
    }

    private boolean successfulNewAccount(int check)
    {
        if (check > 0){
            // display success notification
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "New Account created!", ButtonType.OK);
            alert.showAndWait();
            loanAmountTextField.clear();
            return true;
        }
        return false;
    }

    /*
    *
    *
    * * * Database Query Functions * * *
    *
    *
    */

    /*  Customer  */
    public static String[][] getAllCustomers()
    {
        String[][] allCustomers = new String[100][10];
        int index = 0;

        try
        {
            String query = "select * from customer";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            //String custID, String Fname, String Lname, String address, String phoneNum, Date birthday, int age
            while (rs.next())
            {
                allCustomers[index][0] = rs.getString("customerID");
                allCustomers[index][1] = rs.getString("customerFname");
                allCustomers[index][2] = rs.getString("customerLname");
                allCustomers[index][3] = rs.getString("customerAddress");
                allCustomers[index][4] = rs.getString("customerCity");
                allCustomers[index][5] = rs.getString("customerState");
                allCustomers[index][6] = rs.getString("customerZip");
                allCustomers[index][7] = rs.getString("customerPhone");
                //allCustomers[index][8] = rs.getDate("customerBirthday");

                index++;
            }
            st.close();
        }
        catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }

        return allCustomers;
    }

    public static int createNewCustomer(String firstName, String lastName, String phoneNum, LocalDate birthday, String address, String city, String state, String zip)
    {
        DateTimeFormatter form = DateTimeFormatter.ofPattern("MM/dd/YYYY");
        int count = 0;

        try
        {
            String query = "INSERT INTO Customer (customerFname, customerLname, customerAddress, customerCity, customerState, customerZip, customerPhone, customerBirthday) " +
                    "VALUES('"+firstName+"', '"+lastName+"', '"+address+"', '"+city+"', '"+state+"', '"+zip+"', '"+phoneNum+"', STR_TO_DATE('"+form.format(birthday)+"', '%m/%d/%Y'));";
            Statement st = connection.createStatement();
            int rs = st.executeUpdate(query);

            if(rs > 0)
            {
                count++;
            }
            st.close();
        }
        catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }

        return count;
    }

    public static int modifyCustomer(String id, String firstName, String lastName, String phoneNum, LocalDate birthday, String address, String city, String state, String zip)
    {
        DateTimeFormatter form = DateTimeFormatter.ofPattern("MM/dd/YYYY");
        int count = 0;

        try
        {
            String query = "UPDATE Customer " +
                    "SET customerFname = '"+firstName+"', customerLname = '"+lastName+"', customerAddress = '"+address+"', customerCity = '"+city+"', customerState = '"+state+"', customerZip = '"+zip+"', customerPhone = '"+phoneNum+"', customerBirthday = STR_TO_DATE('"+form.format(birthday)+"', '%m/%d/%Y')" +
                    "WHERE customerID = "+id+";";
            Statement st = connection.createStatement();
            int rs = st.executeUpdate(query);

            if(rs > 0)
            {
                count++;
            }
            st.close();
        }
        catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }

        return count;
    }

    public static String[][] searchCustomer(String searchTerm)
    {
        String[][] searchedCustomers = new String[10][100];
        int index = 0;

        try
        {
            String query = "SELECT * FROM Customer WHERE customerFname LIKE '%"+searchTerm+"%' OR customerLname LIKE '%"+searchTerm+"%';";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next())
            {
                searchedCustomers[index][0] = rs.getString("customerID");
                searchedCustomers[index][1] = rs.getString("customerFname");
                searchedCustomers[index][2] = rs.getString("customerLname");
                searchedCustomers[index][3] = rs.getString("customerAddress");
                searchedCustomers[index][4] = rs.getString("customerCity");
                searchedCustomers[index][5] = rs.getString("customerState");
                searchedCustomers[index][6] = rs.getString("customerZip");
                searchedCustomers[index][7] = rs.getString("customerPhone");
                //searchedCustomers[index][8] = rs.getDate("customerBirthday");

                index++;
            }
            st.close();
        }
        catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }

        return searchedCustomers;
    }

    /*  Account  */
    public static String[][] getAllAccounts(String custID)
    {
        String[][] allAccounts = new String[6][100];
        int index = 0;

        try
        {
            String query = "select * from customeraccounts where customerID = '"+custID+"'";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next())
            {
                allAccounts[index][0] = rs.getString("accountID");
                allAccounts[index][1] = rs.getString("customerID");
                allAccounts[index][2] = rs.getString("accountType");
                //allAccounts[index][3] = rs.getDate("creationDate");
                allAccounts[index][4] = String.valueOf(rs.getDouble("amount"));

                index++;
            }
            st.close();
        }
        catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }

        return allAccounts;
    }

    public static int createNewAccount(String custID, String accountType, LocalDate creationDate, String amount)
    {
        DateTimeFormatter form = DateTimeFormatter.ofPattern("MM/dd/YYYY");
        int count = 0;

        double convAmount = Double.parseDouble(amount);

        try
        {
            String query = "INSERT INTO CustomerAccounts (customerID, accountType, creationDate, amount) " +
                    "VALUES('"+custID+"', '"+accountType+"', STR_TO_DATE('"+form.format(creationDate)+"', '%m/%d/%Y'), "+convAmount+");";
            Statement st = connection.createStatement();
            int rs = st.executeUpdate(query);

            if(rs > 0)
            {
                count++;
            }
            st.close();
        }
        catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }

        return count;
    }

    /*  DepositWithdraw  */
    public static int createNewDWEntry(String accountID, LocalDate date, String amount, char type){
        DateTimeFormatter form = DateTimeFormatter.ofPattern("MM/dd/YYYY");
        int count = 0;
        if (type == 'w')
            amount = "-"+amount;

        try{
            String query = String.format("INSERT INTO depositewithdraw(accountid, actiontype, dwDate, amount) VALUES(%s, '%c', STR_TO_DATE('%s', '%%m/%%d/%%Y'), %s);", accountID, type, form.format(date), amount);
            Statement st = connection.createStatement();
            int rs = st.executeUpdate(query);

            if(rs > 0)
            {
                count++;
            }
            st.close();
        }catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }
        return count;

    }

    /*  Transfer  */
    public static int newTransfer(String fromID, String toID, LocalDate date, String amount){
        DateTimeFormatter form = DateTimeFormatter.ofPattern("MM/dd/YYYY");
        int count = 0;
        double doubleAmount = Double.parseDouble(amount);

        try{
            String query = String.format("INSERT INTO Transfer (senderAccountID, receiverAccountID, transferdate, transferAmount) VALUES(%s, %s, STR_TO_DATE('%s', '%%m/%%d/%%Y'), %.2f);", fromID, toID, form.format(date), doubleAmount);
            Statement st = connection.createStatement();
            int rs = st.executeUpdate(query);

            if(rs > 0)
            {
                count++;
            }
            st.close();
        }catch(Exception e)
        {
            //Logger logger = Logger.getLogger(Employee.class.getName());
            //logger.log(Level.SEVERE, "Failed to connect to database:", e);
        }
        return count;
    }

    /*  Database Connection  */
    public static Connection setupConnection()
    {
        final String driver = "com.mysql.jdbc.Driver";
        String dbConnection = "jdbc:mysql://127.0.0.1:3306/bankmanagementsystem";
        String username = "root";
        String password = "root";

        try
        {
            Class.forName(driver);
        }
        catch (Exception e)
        {
            //System.out.println("catch driver");
        }

        try
        {
            connection = DriverManager.getConnection(dbConnection, username, password);
            return connection;
        }
        catch (Exception e)
        {
            //System.out.println("catch connection");
        }

        return connection;
    }
}
