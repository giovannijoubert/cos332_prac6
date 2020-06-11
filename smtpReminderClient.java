import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class smtpReminderClient {
    public static void main(String[] args) { 
        List<String> EmailBody = new ArrayList<String>();      
         //open eventfile
         try {
            File myObj = new File("eventfile.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
            
              String data = myReader.nextLine();
              String eventDate = data.substring(0, data.indexOf(" "));
              String month = eventDate.split("/")[0];
              if(month.length() == 1)
                month = "0" + month;

              String day = eventDate.split("/")[1];
              if(day.length() == 1)
                day = "0" + day;

                //check for events within the next 6 days.
                if(daysBetween(day + " " + month + " "+ LocalDate.now().getYear()) >=0 )
                    if(daysBetween(day + " " + month + " "+ LocalDate.now().getYear()) <= 6){
                        //attach to email body
                        EmailBody.add(data.substring(data.indexOf(" ")+1, data.length()));
                    }
                
            }

            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("Coudn't open the eventfile.");
            e.printStackTrace();
          }

        //send the email
        if(EmailBody.size() > 0){
            System.out.println("Event/s within the next 6 days.");
            System.out.println(EmailBody);

            sendEmail(EmailBody);
        } else {
            System.out.println("No events to send.");
        }
        
        
        
    }

    private static void sendEmail(List<String> EmailBody){
        System.out.println("Sending Email (please wait for server)...");
        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            pingSocket = new Socket("sitecheck.co.za", 25);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));

            //greet server
            out.println("ehlo sitecheck.co.za");
           
            //wait for server to respond
            Thread.sleep(3000);

            //authenticate SMTP
            out.println("AUTH LOGIN");

            //wait for server to respond
            Thread.sleep(2000);

            //send username
            out.println("Y29zMzMyc2VuZGVyQHNpdGVjaGVjay5jby56YQ==");

            //wait for server to respond
            Thread.sleep(2000);

            //send password
            out.println("KnEkKHRpIVBTVXlf");

            //wait for server to respond
            Thread.sleep(2000);

            
            out.println("mail from: cos332sender@sitecheck.co.za");
            out.println("rcpt to: giovanni.joubert@gmail.com");
            out.println("data");
            out.println("To: Giovanni giovanni.joubert@gmail.com");
            out.println("From: COS332Sender cos332sender@sitecheck.co.za");
            out.println("Subject: Events & Birthday's This week");
            out.println("Hi! Here are the events & birthday's for this week");
            
            for (String event : EmailBody) {
                out.println("\t - " + event);
            }
            out.println("");
            out.println("Regards,");
            out.println("Your COS332 Mailer");
            out.println(".");
            
            out.println("quit");
            

            String inputLine;

            //Print server responses
            while ((inputLine = in.readLine()) != null) 
                System.out.println(inputLine);

            out.close();
            in.close();
            pingSocket.close();
        } catch (IOException e) {
            return;
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //returns amount of 24 hour days between input date and current date
    private static long daysBetween(String input){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
        //get system date
        LocalDate currentDate = LocalDate.now();
        
        LocalDate inputDate = LocalDate.parse(input, dtf);
        long daysBetween = Duration.between(currentDate.atStartOfDay(), inputDate.atStartOfDay()).toDays();
        return daysBetween;
    }
}