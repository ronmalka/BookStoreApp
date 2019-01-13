package bgu.spl.mics.application.passiveObjects;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Deserialize {
    public static void main(String[] args) {
        try {
            //Customer
            HashMap<Integer,Customer> customers;
            // Reading teh object from a file
            FileInputStream file3 = new FileInputStream(args[0]);
            ObjectInputStream in3 = new ObjectInputStream(file3);

            // Method for deserialization of object
            customers = (HashMap<Integer,Customer>)in3.readObject();
            in3.close();
            file3.close();
            printCustomers(customers);
            //BookAmount
            HashMap<String,Integer> bookAmount;
            // Reading teh object from a file
            FileInputStream file = new FileInputStream(args[1]);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            bookAmount = (HashMap <String,Integer>)in.readObject();
            in.close();
            file.close();
            printBookAmount(bookAmount);
            //OrderReceipt
            LinkedList<OrderReceipt> orders;
            // Reading teh object from a file
            FileInputStream file2 = new FileInputStream(args[2]);
            ObjectInputStream in2 = new ObjectInputStream(file2);

            // Method for deserialization of object
            orders = (LinkedList<OrderReceipt>)in2.readObject();
            in2.close();
            file2.close();
            printOrderReceipt(orders);


            //MoneyRegister
            MoneyRegister moneyRegister;
            // Reading teh object from a file
            FileInputStream file4 = new FileInputStream(args[3]);
            ObjectInputStream in4 = new ObjectInputStream(file4);

            // Method for deserialization of object
            moneyRegister = (MoneyRegister)in4.readObject();
            in4.close();
            file4.close();
            printMoneyRegister(moneyRegister);

        }

        catch (IOException ex) {
            System.out.println("IOException is caught");
        }

        catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException" + " is caught");
        }
    }
    private static void printBookAmount(HashMap <String,Integer> bookAmount){
        Iterator it = bookAmount.entrySet().iterator();
        System.out.println("********** Book Amount **********");
        while (it.hasNext()){
            HashMap.Entry book = (HashMap.Entry)it.next();
            System.out.println("The book name: " + (String)book.getKey() + " and his amount: " +(Integer)book.getValue() );
        }

    }
    private static void printOrderReceipt(LinkedList<OrderReceipt> orders){
        System.out.println("********** Order Receipt **********");
        for(OrderReceipt order: orders){
            System.out.println(order.toString());
        }

    }
    private static void printCustomers(HashMap<Integer,Customer> customers){
        System.out.println("********** Customers **********");
        Iterator it = customers.entrySet().iterator();
        while (it.hasNext()){
            HashMap.Entry customer = (HashMap.Entry)it.next();
            int customerID = (Integer)customer.getKey();
            String customerName = ((Customer)customer.getValue()).getName();
            int amount = ((Customer)customer.getValue()).getAvailableCreditAmount();
            System.out.println("The customer id: " + customerID + " and his name: " + customerName + " amount: " + amount);
        }
    }
    private static void printMoneyRegister(MoneyRegister moneyRegister){
        System.out.println("********** MoneyRegister **********");
        System.out.println(moneyRegister.toString());

    }

}
