package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */

public class BookStoreRunner {

    public static void main(String[] args) {

        HashMap<Integer,Customer> customersOutput = new HashMap<>();
        String path = args[0];
        FileOutputStream customersFileOut = null;
        ObjectOutputStream customersOut = null;
        FileOutputStream MoneyRegisterFileOut = null;
        ObjectOutputStream MoneyRegisterOut = null;
        BufferedReader bufferedReader = null;
        OrderId orderId = new OrderId();
        ArrayList<Thread> threads = new ArrayList<>();
        try {
            //JSON parse
            bufferedReader = new BufferedReader(new FileReader(path));
            Gson gson = new Gson();
            LinkedTreeMap json = (LinkedTreeMap)gson.fromJson(bufferedReader, Object.class);
            //Parse the number of the services
            int numSelling = ((Double)((LinkedTreeMap)json.get("services")).get("selling")).intValue() ;
            int numInventoryService = ((Double)((LinkedTreeMap)json.get("services")).get("inventoryService")).intValue();
            int numLogistics = ((Double)((LinkedTreeMap)json.get("services")).get("logistics")).intValue();
            int numResourcesService = ((Double)((LinkedTreeMap)json.get("services")).get("resourcesService")).intValue();
            int numApiServices = ((ArrayList)((LinkedTreeMap)json.get("services")).get("customers")).size() ;
            int totalServices = numSelling + numInventoryService + numLogistics + numResourcesService + numApiServices;
            CountDownLatch latch = new CountDownLatch(totalServices);
            //Parse TimeService
            int timeSpeed = ((Double)(((LinkedTreeMap)((LinkedTreeMap)json.get("services")).get("time")).get("speed"))).intValue();
            int timeDuration = ((Double)(((LinkedTreeMap)((LinkedTreeMap)json.get("services")).get("time")).get("duration"))).intValue();
            MicroService timeService = new TimeService(timeSpeed,timeDuration,latch,1);
            Thread timeThread = new Thread(timeService);
            timeThread.start();
            threads.add(timeThread);
            //Parse BookInventoryInfo
            ArrayList<LinkedTreeMap> list = (ArrayList<LinkedTreeMap>) json.get("initialInventory");
            BookInventoryInfo[] books = new BookInventoryInfo[list.size()];
            for(int i = 0; i < list.size(); i++){
                books[i] = new BookInventoryInfo((String) list.get(i).get("bookTitle"), ((Double) list.get(i).get("amount")).intValue(), ((Double) list.get(i).get("price")).intValue());
            }
            Inventory.getInstance().load(books);
            //Parse DeliveryVehicle
            ArrayList<LinkedTreeMap> vehiclesList =(ArrayList<LinkedTreeMap>)((ArrayList<LinkedTreeMap>) json.get("initialResources")).get(0).get("vehicles");
            DeliveryVehicle[] vehicles = new DeliveryVehicle[vehiclesList.size()];
            for (int i = 0; i < vehiclesList.size(); i++) {
                int license =((Double) vehiclesList.get(i).get("license")).intValue();
                int speed = ((Double) vehiclesList.get(i).get("speed")).intValue();
                vehicles[i] = new DeliveryVehicle(license,speed);
            }
            ResourcesHolder.getInstance().load(vehicles);
            //Set the SellingService threads
            for(int i = 0; i < numSelling; i++){
                MicroService sellingService = new SellingService(latch, i);
                Thread thread = new Thread(sellingService);
                thread.start();
                threads.add(thread);
            }
            //Set the InventoryService threads
            for(int i = 0; i < numInventoryService; i++){
                MicroService inventoryService = new InventoryService(latch, i);
                Thread thread = new Thread(inventoryService);
                thread.start();
                threads.add(thread);
            }
            //Set the LogisticsService threads
            for(int i = 0; i < numLogistics; i++){
                MicroService logistics = new LogisticsService(latch, i);
                Thread thread = new Thread(logistics);
                thread.start();
                threads.add(thread);
            }
            //Set the ResourceService threads
            for(int i = 0; i < numResourcesService; i++){
                MicroService resourcesService = new ResourceService(latch, i);
                Thread thread = new Thread(resourcesService);
                thread.start();
                threads.add(thread);
            }
            //Parse Customers
            for(int i = 0; i < numApiServices; i++){
                LinkedTreeMap JSONcustomer = (LinkedTreeMap)((ArrayList) ((LinkedTreeMap)json.get("services")).get("customers")).get(i);
                int customerID = ((Double) JSONcustomer.get("id")).intValue();
                String customerName = (String) JSONcustomer.get("name");
                String customerAdress = (String) JSONcustomer.get("address");
                int customerCreditCardNumber = ((Double)((LinkedTreeMap) JSONcustomer.get("creditCard")).get("number")).intValue();
                int customerCreditCardAmount = ((Double)((LinkedTreeMap) JSONcustomer.get("creditCard")).get("amount")).intValue();
                int customerDistance = ((Double) JSONcustomer.get("distance")).intValue();
                Customer customer = new Customer(customerID, customerName, customerAdress, customerDistance, customerCreditCardNumber, customerCreditCardAmount);
                for(int j = 0; j <  ((ArrayList)JSONcustomer.get("orderSchedule")).size(); j++){
                    String bookName = (String)((LinkedTreeMap)((ArrayList) JSONcustomer.get("orderSchedule")).get(j)).get("bookTitle");
                    int tick = ((Double)((LinkedTreeMap)((ArrayList) JSONcustomer.get("orderSchedule")).get(j)).get("tick")).intValue();
                    customer.addOrderSchedule(bookName, tick);
                }
                //Set the APIService threads
                MicroService apiService = new APIService(customer , latch,orderId, i);
                Thread thread = new Thread(apiService);
                thread.start();
                threads.add(thread);
                customersOutput.put(customerID,customer);
            }
            //Main Thread join
            for(Thread thread: threads){
                thread.join();
            }
            //Serialize Customer
            customersFileOut = new FileOutputStream(args[1]);
            customersOut = new ObjectOutputStream(customersFileOut);
            customersOut.writeObject(customersOutput);
            //Serialize BookAmount
           Inventory.getInstance().printInventoryToFile(args[2]);
            //Serialize OrderReceipts
           MoneyRegister.getInstance().printOrderReceipts(args[3]);
            //Serialize MoneyRegister
            MoneyRegisterFileOut = new FileOutputStream(args[4]);
            MoneyRegisterOut = new ObjectOutputStream(MoneyRegisterFileOut);
            MoneyRegisterOut.writeObject(MoneyRegister.getInstance());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                customersOut.close();
                MoneyRegisterOut.close();
                customersFileOut.close();
                MoneyRegisterFileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}


