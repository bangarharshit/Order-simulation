package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

public class SafeBookingWithLock {

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        for (int i = 0; i < 150; i++) {
            Thread thread = new Thread(() -> {
                bookWithLock(successCount, failureCount);
            });
            thread.start();
        }
        while (true) {
            Thread.sleep(500);
        }
    }

    public static void bookWithLock(AtomicInteger successCount, AtomicInteger failureCount) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "postgres");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            stmt.execute("BEGIN");
            String sql = "select * from tickets where status='not_taken' for update skip locked limit 1";
            ResultSet resultSet = stmt.executeQuery(sql);
            int id = -1;
            while ( resultSet.next() ) {
                id = resultSet.getInt("ticket_id");
                System.out.println(id);
            }
            if(id != -1) {
                System.out.println("total taken"  + successCount.incrementAndGet());
                System.out.println("id taken " + id);
                stmt.executeUpdate("update tickets set status='taken' where ticket_id=" + id);
            } else {
                System.out.println("not taken" + failureCount.incrementAndGet());
            }
            stmt.execute("COMMIT");
            resultSet.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        }
    }
}