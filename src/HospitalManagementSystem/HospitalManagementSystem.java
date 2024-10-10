package HospitalManagementSystem;

import javax.print.Doc;
import java.sql.*;
import java.util.Scanner;
import java.util.SortedMap;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital1";
    private static final String username = "root";
    private static final String password = "kunal@1440";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice : ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        //Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //Book Appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Thanks for using HMS");
                        return;
                    default:
                        System.out.println("Enter valid choice");
                        break;
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }


    }
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.print("Enter Patient ID: ");
        int patientID = scanner.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorID = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String date = scanner.next();
        if (patient.getPatientByID(patientID) && doctor.getDoctorByID(doctorID)){
            if (checkDoctorAvailability(doctorID, date, connection)){
                String appointmentquery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentquery);
                    preparedStatement.setInt(1, patientID);
                    preparedStatement.setInt(2, doctorID);
                    preparedStatement.setString(3, date);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows>0){
                        System.out.println("Appointment booked!");
                    }else {
                        System.out.println("Failed to book appointment!!!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor not available on selected date!!");
            }
        }else {
            System.out.println("Either doctor or patient doesn't exist!!");
        }
    }
    public static boolean checkDoctorAvailability(int doctor_ID, String date, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        // COUNT(*) == it returns the number of rows corresponding to above query//
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctor_ID);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1); // its denote index of column we can also provide column name
                if (count==0){
                    return true;
                }else {
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
