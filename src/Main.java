import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends JFrame {
    private JComboBox<String> playerIdComboBox;

    private String gameTitleField;
    private String gameIdField;
    private String playerIDField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JTextField provinceField;
    private JTextField postalCodeField;
    private JTextField phoneNumberField;

    private JButton insertButton, updateButton, reportButton, insertGame, insertPlayer;

    public Main() {
        setTitle("Game and Player Information");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1));

        insertButton = new JButton("Insert Information");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {

                // Logic for inserting game information
                String gameID = JOptionPane.showInputDialog("Enter Game ID:");
                String PlayerGameId = JOptionPane.showInputDialog("Enter Player Game ID:");
                String PlayerID = JOptionPane.showInputDialog("Enter Player ID:");
                String FirstName = JOptionPane.showInputDialog("Enter First Name:");
                String LastName = JOptionPane.showInputDialog("Enter Last Name:");
                String PostalCode = JOptionPane.showInputDialog("Enter Postal Code:");
                String Address = JOptionPane.showInputDialog("Enter Address:");
                String Province = JOptionPane.showInputDialog("Enter Province:");
                String PhoneNumber = JOptionPane.showInputDialog("Enter Phone Number:");
                Date PlayingDate = null;
                try {
                    String dateString = JOptionPane.showInputDialog("Enter Playing Date (YYYY-MM-DD):");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    PlayingDate = dateFormat.parse(dateString);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                String Score = JOptionPane.showInputDialog("Enter Score:");

                try {
                    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@oracle1.centennialcollege.ca:1521:SQLD",
                            "COMP228_W24_dl_14", "password");

                    String insertGamePlayerQuery = "INSERT INTO playerandgame ( player_game_id, game_id, player_id, playing_date, score ) VALUES (?, ?, ?, ?, ?)";

                    PreparedStatement insertStatement = conn.prepareStatement(insertGamePlayerQuery);
                    insertStatement.setString(1, PlayerGameId);
                    insertStatement.setString(2, gameID);
                    insertStatement.setString(3, PlayerID);
                    insertStatement.setDate(4, new java.sql.Date(PlayingDate.getTime()));
                    insertStatement.setString(5, Score);

                    String InsertPlayerQuery = "INSERT INTO Player (player_id, first_name, last_name, postal_code, address, province, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement insertPlayerStatement = conn.prepareStatement(InsertPlayerQuery);
                    insertPlayerStatement.setString(1, PlayerID);
                    insertPlayerStatement.setString(2, FirstName);
                    insertPlayerStatement.setString(3, LastName);
                    insertPlayerStatement.setString(4, PostalCode);
                    insertPlayerStatement.setString(5, Address);
                    insertPlayerStatement.setString(6, Province);
                    insertPlayerStatement.setString(7, PhoneNumber);

                    int affectedRows2 = insertPlayerStatement.executeUpdate();

                    int affectedRows = insertStatement.executeUpdate();

                    if (affectedRows > 0 && affectedRows2 > 0) {
                        JOptionPane.showMessageDialog(null, "Game information inserted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to insert game information.");
                    }

                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        updateButton = new JButton("Update Player Information");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String UpdatePlayerInfo = "UPDATE Player SET address = ?, postal_code = ?, province = ?, phone_number = ? WHERE player_id = ?";
                // Logic for inserting player information
                String PlayerID = JOptionPane.showInputDialog("Enter Player ID:");
                String Address = JOptionPane.showInputDialog("Enter New Address:");
                String PostalCode = JOptionPane.showInputDialog("Enter New Postal Code:");
                String Province = JOptionPane.showInputDialog("Enter New Province:");
                String PhoneNumber = JOptionPane.showInputDialog("Enter New Phone Number:");

                try {
                    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@oracle1.centennialcollege.ca:1521:SQLD",
                            "COMP228_W24_dl_14", "password");


                    PreparedStatement insertStatement = conn.prepareStatement(UpdatePlayerInfo);
                    insertStatement.setString(1, Address);
                    insertStatement.setString(2, PostalCode);
                    insertStatement.setString(3, Province);
                    insertStatement.setString(4, PhoneNumber);
                    insertStatement.setString(5, PlayerID);

                    int affectedRows = insertStatement.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(null, "Game information inserted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to insert game information.");
                    }

                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        reportButton = new JButton("View Report");
        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPlayerIdSelectionDialog();
            }
        });

        panel.add(insertButton);
        panel.add(updateButton);
        panel.add(reportButton);

        add(panel);
        setVisible(true);
    }

    private void openPlayerIdSelectionDialog() {
        JDialog dialog = new JDialog(this, "Select Player ID", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new FlowLayout());

        playerIdComboBox = new JComboBox<>();
        fetchPlayerIDs();

        JButton showReportButton = new JButton("Show Report");
        showReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedPlayerId = (String) playerIdComboBox.getSelectedItem();
                displayReport(selectedPlayerId);
            }
        });

        dialog.add(playerIdComboBox);
        dialog.add(showReportButton);
        dialog.setVisible(true);
    }

    private void fetchPlayerIDs() {
        try{
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@oracle1.centennialcollege.ca:1521:SQLD",
                    "COMP228_F23_piy_23", "password");

            String query="select distinct player_id from playerandgame order by player_id";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                //shows topic data in combobox
                playerIdComboBox.addItem(rs.getString("Player_ID"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private void displayReport(String selectedPlayerId) {
        // Display report based on the selected player ID
        try {
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@oracle1.centennialcollege.ca:1521:SQLD",
                    "COMP228_F23_piy_23", "password");

            String selectQuery = "SELECT Player.player_id, Player.first_name, Player.last_name, " +
                    "PlayerAndGame.playing_date, Game.game_title, PlayerAndGame.score " +
                    "FROM Player " +
                    "INNER JOIN PlayerAndGame ON Player.player_id = PlayerAndGame.player_id " +
                    "INNER JOIN Game ON PlayerAndGame.game_id = Game.game_id " +
                    "WHERE Player.player_id = ?";

            PreparedStatement selectStatement = conn.prepareStatement(selectQuery);
            selectStatement.setString(1, selectedPlayerId);

            ResultSet resultSet = selectStatement.executeQuery();

            StringBuilder reportInfo = new StringBuilder();
            reportInfo.append("Player ID\tFirst Name\tLast Name\tPlaying Date\tGame Title\tScore\n");

            while (resultSet.next()) {
                reportInfo.append(resultSet.getString("player_id")).append("\t")
                        .append(resultSet.getString("first_name")).append("\t")
                        .append(resultSet.getString("last_name")).append("\t")
                        .append(resultSet.getDate("playing_date")).append("\t")
                        .append(resultSet.getString("game_title")).append("\t")
                        .append(resultSet.getInt("score")).append("\n");
            }

            JOptionPane.showMessageDialog(null, new JTextArea(reportInfo.toString()));

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}