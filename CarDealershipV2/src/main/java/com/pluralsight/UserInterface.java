package com.pluralsight;

import com.pluralsight.contracts.*;

import java.util.ArrayList;

public class UserInterface {

    public static String filename_dealership = "inventory.csv";
    public static String filename_contracts = "contracts.csv";
    public Dealership currentDealership;
    public ArrayList<Contract> contracts;

    public UserInterface(){
        currentDealership = DealershipFileManager.getFromCSV(filename_dealership);
        contracts = ContractFileManager.getFromCSV(filename_contracts);
    }


    public void display(){

        String options = """
                Please select from the following choices:
                1 - Find vehicles within a price range
                2 - Find vehicles by make / model
                3 - Find vehicles by year range
                4 - Find vehicles by color
                5 - Find vehicles by mileage range
                6 - Find vehicles by type (car, truck, SUV, van)
                7 - List ALL vehicles
                8 - Add a vehicle
                9 - Remove a vehicle
                10 - Sell or Lease vehicle
                11 - Display Contracts
                99 - Quit

                >>>\s""";

        int selection;

        // User Interface Loop
        do {
            System.out.println("Welcome to " + currentDealership.getName() + "!");
            selection = Console.PromptForInt(options);
            switch (selection) {
                case 1 -> processGetByPriceRequest();
                case 2 -> processGetByMakeModelRequest();
                case 3 -> processGetByYearRequest();
                case 4 -> processGetByColorRequest();
                case 5 -> processGetByMileageRequest();
                case 6 -> processGetByVehicleTypeRequest();
                case 7 -> processGetAllVehiclesRequest();
                case 8 -> processAddVehicleRequest();
                case 9 -> processRemoveVehicleRequest();
                case 10 -> processSellOrLeaseRequest();
                case 11 -> processDisplayContractsRequest();
                case 99 -> System.exit(0);
                default -> System.out.println("Invalid selection. Please try again.");
            }
        } while (selection != 99);



    }

    private void processRemoveVehicleRequest() {
        int vin = Console.PromptForInt("Enter vin number of the vehicle you want to remove: ");
        currentDealership.removeVehicleFromInventory(vin);
    }

    private void processAddVehicleRequest() {
        //get lots of values from the user...
        int vin = Console.PromptForInt("Enter Vin: ");
        int year = Console.PromptForInt("Enter year: ");
        String make = Console.PromptForString("Enter make: ");
        String model = Console.PromptForString("Enter model: ");
        String vehicleType = Console.PromptForString("Enter vehicle type: ");
        String color = Console.PromptForString("Enter color:  ");
        int odometer = Console.PromptForInt("Enter odometer: ");
        double price = Console.PromptForDouble("Enter price: ");

        Vehicle v = new Vehicle(vin,year, make, model, vehicleType, color, odometer, price);

        currentDealership.addVehicleToInventory(v);
        DealershipFileManager.saveToCSV(currentDealership, filename_dealership);

    }

    private void processGetByVehicleTypeRequest() {
        String type = Console.PromptForString("Enter what type of vehicle you are looking for: ");
        for (Vehicle v : currentDealership.getVehiclesByType(type)) {
            displayVehicle(v);
        }
    }

    private void processGetByMileageRequest() {
        int min = Console.PromptForInt("Enter Min Miles: ");
        int max = Console.PromptForInt("Enter Max Miles: ");
        for(Vehicle v : currentDealership.getVehicleByOdometer(min, max)){
            displayVehicle(v);
        }
    }

    private void processGetByColorRequest() {
        String color = Console.PromptForString("Enter Color: ");
        for(Vehicle v : currentDealership.getVehiclesByColor(color)) {
            displayVehicle(v);
        }
    }

    private void processGetByYearRequest() {
        int min = Console.PromptForInt("Enter Start Year: ");
        int max = Console.PromptForInt("Enter End Year: ");
        for(Vehicle v : currentDealership.getVehicleByYear(min, max)){
            displayVehicle(v);
        }
    }

    private void processGetByMakeModelRequest() {
        String make =Console.PromptForString("Enter Make: ");
        String model = Console.PromptForString("Enter Model: ");
        for(Vehicle v : currentDealership.getVehicleByMakeModel(make, model)){
            displayVehicle(v);
        }
    }

    private void processGetByPriceRequest() {
        double min = Console.PromptForDouble("Enter min: ");
        double max = Console.PromptForDouble("Enter max: ");
        for(Vehicle v : currentDealership.getVehiclesByPrice(min, max)){
            displayVehicle(v);
        }
    }


    public void processGetAllVehiclesRequest(){
        for(Vehicle v : currentDealership.getInventory()){
            displayVehicle(v);
        }
    }

    public void processSellOrLeaseRequest(){
        int vin = 0;
        String input;
        // Get all the info we need from the user
        // Get VIN
        do {
            input = Console.PromptForString("Enter VIN of the vehicle to sell/lease (or 'v' to view all vehicles or 'q' to cancel): ");
            if (input.equalsIgnoreCase("q")) return;
            if (input.equalsIgnoreCase("v")) {
                processGetAllVehiclesRequest();
                input = "";
                continue;
            }

            try {
                vin = Integer.parseInt(input);

                Vehicle vehicleToSell = currentDealership.getVehicleByVIN(vin);
                if (vehicleToSell == null) {
                    System.out.println("Vehicle not found. Please try again.");
                    input = ""; // Reset input
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number. or v to list vehicles, or q to return to main menu.");
                input = ""; // Reset input
            }
        } while (input.isEmpty());

        System.out.println(vin);
        System.out.println(currentDealership.getVehicleByVIN(vin));
        //at this point we should have a vin...


        // Get contract type
        String contractType;
        do {
            contractType = Console.PromptForString("Enter contract type (sale/lease) (or 'q' to cancel): ");
            if (contractType.equalsIgnoreCase("q")) return;
            if (!contractType.equalsIgnoreCase("sale") && !contractType.equalsIgnoreCase("lease")) {
                System.out.println("Invalid contract type. Please enter 'sale' or 'lease'.");
                contractType = ""; // Reset input
            }
        } while (contractType.isEmpty());



        // Get customer name
        String customerName;
        do {
            customerName = Console.PromptForString("Enter customer name (or 'q' to cancel): ");
            if (customerName.equalsIgnoreCase("q")) return;
        } while (customerName.isEmpty());

        // Get customer email
        String customerEmail;
        do {
            customerEmail = Console.PromptForString("Enter customer email (or 'q' to cancel): ");
            if (customerEmail.equalsIgnoreCase("q")) return;
        } while (customerEmail.isEmpty());

        // Get date
        String date;
        do {
            date = Console.PromptForString("Enter date (YYYYMMDD) (or 'q' to cancel): ");
            if (date.equalsIgnoreCase("q")) return;

            // Validate date format
            if (date.length() != 8 || !date.matches("\\d{8}")) {
                System.out.println("Invalid date format. Please use YYYYMMDD (e.g., 20210928)");
                date = ""; // Reset input
                continue;
            }
        } while (date.isEmpty());

        Vehicle vehicle = currentDealership.getVehicleByVIN(vin);

        Contract contract = null;

        // Create appropriate contract type
        if (contractType.equalsIgnoreCase("sale")) {
            String financeInput;
            boolean isFinanced;
            do {
                financeInput = Console.PromptForString("Will this be financed? (yes/no) (or 'q' to cancel): ");
                if (financeInput.equalsIgnoreCase("q")) return;
                if (financeInput.equalsIgnoreCase("yes")) {
                    isFinanced = true;
                    break;
                } else if (financeInput.equalsIgnoreCase("no")) {
                    isFinanced = false;
                    break;
                }
                System.out.println("Please enter 'yes' or 'no'.");
            } while (true);

            contract = new SalesContract(date, customerName, customerEmail, vehicle, isFinanced);
        } else {
            contract = new LeaseContract(date, customerName, customerEmail, vehicle);
        }

        contracts.add(contract);
        ContractFileManager.appendToCSV(contract, filename_contracts);


    }


    public void processDisplayContractsRequest(){
        displayContracts(contracts);
    }

    public void displayVehicle(Vehicle v){
        System.out.println(v);
    }

    public void displayContracts(ArrayList<Contract> contracts){
        for(Contract c : contracts){
            System.out.println(c);
        }
    }


}