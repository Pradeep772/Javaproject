import javax.swing.*; //it contains the classes of the gui components
import java.awt.*;//it contains the classes of the gui components
import java.awt.event.ActionEvent; // to perform the operation
import java.awt.event.ActionListener; // to do the operations after clicking the buttons
import java.io.*;  // to perform operations with csv files
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption; // the above three are used to read write and update and copy the data into csv files
import java.util.ArrayList; // to use arraylist to store the details 
import java.util.List; //same as above
import java.util.Locale; 
//the below  are used for freetts library
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;


public class ATMGUI extends JFrame implements ActionListener {  //ATMGUI is the main class and jframe class is used to create the application window
                                                                 // and actionlistner is an interface to perform the actions like after clicking buttons
    private JTextField textField;  //for writing texts
    private JPasswordField passwordField; //for writing password which is not visible
    private JButton loginButton;  //for creating buttons
    private JButton withdrawButton; //for creating buttons
    private JButton checkBalanceButton;//for creating buttons
    private JButton logoutButton;//for creating buttons
    private JButton changePinButton;//for creating buttons
    private JButton transferButton;//for creating buttons
    private JButton depositButton;//for creating buttons
    private JLabel balanceLabel;//to give a written message
    private JLabel accountnumber;
    private JLabel pin;
    private JPanel loginPanel; //to create login screen
    private JPanel postLoginPanel; //after login this panel is visible
    private JPanel buttonPanel; // to arrange the buttons in an proper order
    private Synthesizer synthesizer;  //to convert text into speech
   

    // Define lists to store account details. 
    private List<String> accountNumbersList = new ArrayList<>();
    private List<String> pinsList = new ArrayList<>();
    private List<String> accountBalancesList = new ArrayList<>(); // Use Integer for balance

    private boolean loggedIn = false;
    private int currentBalance = 0;
    private int accountIndex = -1; // Change balance to int
    int buttonWidth = 180; //to adjust the width of the button
    int buttonHeight = 50; //to adjust the height of the button
   
    public ATMGUI() {
        setTitle("ATM System"); // to set the name of gui screen
        setSize(700, 500);//to set height width of gui screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);//to close after clicking the x button
        setLocationRelativeTo(null);// to appear thw window to the center of the screen

        Font font = new Font("Arial", Font.PLAIN, 20); //to set style of the text
        

        loginPanel = new JPanel(); //creating a jpanel object for loginpage creation
        loginPanel.setLayout(new BorderLayout()); // to set a layout for gui components arrangement
            //By using a BorderLayout, the loginPanel will be divided into five regions: North, South, East, West, and Center.
        //creating a new panel to add the textfields
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 2));
        loginPanel.setOpaque(false);
        
        //creating a label account number
        accountnumber= new JLabel("AccountNumber:");
        accountnumber.setFont(font);
        centerPanel.add(accountnumber);
        
        //creating a text filed
        textField = new JTextField();
        textField.setFont(font);
        centerPanel.add(textField);

        //creating a label pin
        pin = new JLabel("Pin");
        pin.setFont(font);
        centerPanel.add(pin);

        //creating a password field 
        passwordField = new JPasswordField();
        passwordField.setFont(font);
        centerPanel.add(passwordField);
        
        //adding centerpanel to loginpanel
        loginPanel.add(centerPanel, BorderLayout.CENTER);

        //creating login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);// When you use this as the argument to addActionListener, it means that the ATMGUI class itself has implemented the actionPerformed method
        loginButton.setFont(font);
        loginPanel.add(loginButton, BorderLayout.SOUTH);


        postLoginPanel = new JPanel();
        postLoginPanel.setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        //creating withdraw button
        withdrawButton = new JButton("Withdraw");
        withdrawButton.setToolTipText("Click to withdraw funds");
        withdrawButton.addActionListener(this);
        withdrawButton.setFont(font);
        withdrawButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        addComponentToPanel(buttonPanel, withdrawButton, 0, 0, GridBagConstraints.CENTER);

        //creating checkbalance button
        checkBalanceButton = new JButton("Check Balance");
        checkBalanceButton.setToolTipText("Check your current balance");
        checkBalanceButton.addActionListener(this);
        checkBalanceButton.setFont(font);
        checkBalanceButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        addComponentToPanel(buttonPanel, checkBalanceButton, 1, 0, GridBagConstraints.CENTER);

        //creating change pin button
        changePinButton = new JButton("Change PIN");
        changePinButton.setToolTipText("Change your PIN");
        changePinButton.addActionListener(this);
        changePinButton.setFont(font);
        changePinButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        addComponentToPanel(buttonPanel, changePinButton, 0, 1, GridBagConstraints.CENTER);

        //creating logout button
        logoutButton = new JButton("Logout");
        logoutButton.setToolTipText("Logout from your account");
        logoutButton.addActionListener(this);
        logoutButton.setFont(font);
        logoutButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        addComponentToPanel(buttonPanel, logoutButton, 1, 2, GridBagConstraints.CENTER);
        
        //creating deposit button
        depositButton = new JButton("Deposit");
        depositButton.setToolTipText("Deposit into your account");
        depositButton.addActionListener(this);
        depositButton.setFont(font);
        depositButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        addComponentToPanel(buttonPanel, depositButton, 1, 1, GridBagConstraints.CENTER);

        //creating transfer button
        transferButton = new JButton("Transfer");
        transferButton.setToolTipText("Transfer money to another account");
        transferButton.addActionListener(this);
        transferButton.setFont(font);
        transferButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        addComponentToPanel(buttonPanel, transferButton, 0, 2, GridBagConstraints.CENTER);

        postLoginPanel.add(buttonPanel, BorderLayout.CENTER);//adding button panel to post login panel.
 
        //creating the balance label
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Adjust the font size
        balanceLabel.setForeground(Color.BLUE);
        postLoginPanel.add(balanceLabel, BorderLayout.NORTH);
        
        //loads the data from the csv
        loadAccountDataFromCSV();
        
        //login page will come 
        add(loginPanel);

        initializeVoiceSynthesis();
        speak("Welcome to A T M");
        
    }
    
    //class to add buttons to the button panel
    private void addComponentToPanel(JPanel panel, Component component, int gridx, int gridy, int anchor) {
        //GridBagConstraints is to set the buttons in the proper order.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = anchor;
        gbc.fill = GridBagConstraints.VERTICAL;
        //
        panel.add(component, gbc);
    }

    private void loadAccountDataFromCSV() {
        //giving csv file path.
        String csvFilePath = "account_data.csv"; 

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) { //reading csv file.
            String line;
            while ((line = br.readLine()) != null) {//readline method:to read the data from the file
                String[] values = line.split(",");
                if (values.length == 3) {
                    accountNumbersList.add(values[0]);
                    pinsList.add(values[1]);
                    accountBalancesList.add((values[2])); // Parse as Integer
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load account data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAccountDataToCSV() {
        String csvFilePath = "account_data.csv"; // Replace with your actual path
        String tempFilePath = "account_data_temp.csv";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath))) { // to write in the file.
            for (int i = 0; i < accountNumbersList.size(); i++) {
                // Convert balance to integer before writing to CSV
                String balance = (accountBalancesList.get(i));
                bw.write(accountNumbersList.get(i) + "," + pinsList.get(i) + "," + balance);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save account data", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Replace the original CSV file with the temporary file
        try {
            Files.move(Path.of(tempFilePath), Path.of(csvFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update account data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
   
    
    public void actionPerformed(ActionEvent e) {
        
        if (!loggedIn) {
            if (e.getSource() == loginButton) { // e is the event that occured by clicking the button
                String accountNumber = textField.getText();
                String pin = new String(passwordField.getPassword());

                accountIndex = accountNumbersList.indexOf(accountNumber);
                if (accountIndex != -1 && pin.equals(pinsList.get(accountIndex))) {
                    loggedIn = true;
                    currentBalance = Integer.parseInt(accountBalancesList.get(accountIndex));
                    loginPanel.setVisible(false);
                    add(postLoginPanel);
                    
                    // speak("Login successful. Welcome, account number " + accountNumber);
                   speak("Log in successful" );
                } else {
                    speak("In valid log in. Please try again.");
                    JOptionPane.showMessageDialog(this, "Invalid Login", "Error", JOptionPane.ERROR_MESSAGE);
                     
                }
            }
        } else {
            if (e.getSource() == withdrawButton) {
                String amountStr = JOptionPane.showInputDialog(this, "Enter Withdrawal Amount:");
                try {
                    int amount = Integer.parseInt(amountStr);

                    if (amount > 0 && amount <= currentBalance) {
                        currentBalance -= amount;
                        

                        // Update the balance in memory
                        accountBalancesList.set(accountIndex, String.valueOf(currentBalance));

                        // Save the updated balance
                        saveAccountDataToCSV();
                        balanceLabel.setText("Current Balance: " + currentBalance);
                        speak("With drawl of  "+amount+" rupees is success ful. ");
                    } else {
                        speak("Invalid amount format. Please try again.");
                        JOptionPane.showMessageDialog(this, "Invalid Withdrawal Amount", "Error", JOptionPane.ERROR_MESSAGE);
                         
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                     speak("Invalid withdrawal amount. Please try again.");
                }
            } else if (e.getSource() == checkBalanceButton) {
                balanceLabel.setText("Current Balance: " + currentBalance);
              speak("Your current balance is " + currentBalance + " rupees.");
            }
             else if (e.getSource() == changePinButton) {
                String newPin = JOptionPane.showInputDialog(this, "Enter New PIN:");
              
                if (newPin != null && !newPin.isEmpty()) {
                    int accountIndex = accountNumbersList.indexOf(textField.getText());
                    if (accountIndex != -1) {
                        // Update the PIN in memory
                        pinsList.set(accountIndex, newPin);

                        // Save the updated data to the CSV file
                        saveAccountDataToCSV();

                        JOptionPane.showMessageDialog(this, "PIN changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                        speak("PIN changed success fully.");
                   }
                }
                
            } else if (e.getSource() == logoutButton) {
                loggedIn = false;
                currentBalance = 0;
                postLoginPanel.setVisible(false);
                loginPanel.setVisible(true);
                textField.setText("");
                passwordField.setText("");
                balanceLabel.setText("");
                accountIndex = -1;
               
                speak("You have been log ged out. Thank you for using the A T M.");
            }
            else if(e.getSource()==depositButton)
            {
                String depositAmountStr = JOptionPane.showInputDialog(this, "Enter Amount to deposit :");
                if (depositAmountStr != null && !depositAmountStr.isEmpty()) {
                    try {
                        int depositAmount = Integer.parseInt(depositAmountStr);
            
                        if (depositAmount > 0) {
                            currentBalance += depositAmount;
                            accountBalancesList.set(accountIndex, String.valueOf(currentBalance));
                            
                            saveAccountDataToCSV();
                            balanceLabel.setText("Current Balance: " + currentBalance);
                            speak("Deposit of " + depositAmount + "rupees is successful.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid Deposit Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                            speak("Invalid deposit amount format. Please try again.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Deposit Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                        speak("Invalid deposit amount format. Please try again.");
                    }
                }
                
            }
            else if (e.getSource() == transferButton) {
                    String recipientAccount = JOptionPane.showInputDialog(this, "Enter Recipient's Account Number:");
                    
                    if (recipientAccount != null && !recipientAccount.isEmpty()) {
                        if (recipientAccount.equals(accountNumbersList.get(accountIndex))) {
                            JOptionPane.showMessageDialog(this, "You cannot transfer to your own account.", "Error", JOptionPane.ERROR_MESSAGE);
                            speak("You cannot transfer to your own account.");
                            return;  // Exit the transfer operation
                        }
    
                        int recipientIndex = accountNumbersList.indexOf(recipientAccount);
                        
                        if (recipientIndex != -1) {
                            String transferAmountStr = JOptionPane.showInputDialog(this, "Enter Transfer Amount:");
                            
                            if (transferAmountStr != null) {
                                try {
                                    int transferAmount = Integer.parseInt(transferAmountStr);
    
                                    if (transferAmount > 0 && transferAmount <= currentBalance) {
                                        currentBalance -= transferAmount;
                                        accountBalancesList.set(accountIndex, String.valueOf(currentBalance));
    
                                        int recipientBalance = Integer.parseInt(accountBalancesList.get(recipientIndex));
                                        recipientBalance += transferAmount;
                                        accountBalancesList.set(recipientIndex, String.valueOf(recipientBalance));
    
                                        saveAccountDataToCSV();
                                        balanceLabel.setText("Current Balance: $" + currentBalance);
    
                                        speak("Transfer of $" + transferAmount + " to account is successful.");
                                    }
                                    else{
                                       JOptionPane.showMessageDialog(this, "Invalid Transfer Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                                        speak("Invalid transfer amount format. Please try again."); 
                                    }
                                 }catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(this, "Invalid Transfer Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                                        speak("Invalid transfer amount format. Please try again.");
                                    }
                                } 
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(this, "Recipient Account Not Found", "Error", JOptionPane.ERROR_MESSAGE);
                            speak("Recipient account not found. Please try again.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Recipient Account Not Found", "Error", JOptionPane.ERROR_MESSAGE);
                            speak("Recipient account not found. Please try again.");
                        }
                   
                } else {
                    speak("You must be logged in to perform a transfer.");
                    JOptionPane.showMessageDialog(this, "Not Logged In", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    
    
    

    private void initializeVoiceSynthesis() {
        try {
            //It specifies which directory we have to use
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            //here regestering the freetts with javaspeech api
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            //synthesizer converts text to speech
            synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            //it allocates the resources for synthesizer
            synthesizer.allocate();
            synthesizer.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void speak(String message) {
        try {
            synthesizer.resume();
            synthesizer.getSynthesizerProperties().setSpeakingRate(130);//Speed of th speech
            synthesizer.speakPlainText(message, null); //message which it wants to read
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);//waits untill the text was fully spoke
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) { //main metho
        SwingUtilities.invokeLater(() -> {  //this method  is used to create the gui components
            ATMGUI atm = new ATMGUI();  //object creation
            atm.setVisible(true);  //it makes the gui component visible to user.
        });
    }
}
