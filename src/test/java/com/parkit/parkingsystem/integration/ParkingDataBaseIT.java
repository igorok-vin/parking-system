package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
       parkingSpotDAO = new ParkingSpotDAO();
       parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
       ticketDAO = new TicketDAO();
       ticketDAO.dataBaseConfig = dataBaseTestConfig;
       dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        parkingService = new ParkingService(inputReaderUtil,parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    }

    @AfterEach
    private void tearDown() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    /*
    *Done: check that the fare generated and out time are populated correctly in the database
    */
    @Test
    public void testParkingLotExit() throws Exception {
        String licensePlateNumber = "ABCDEF";
        Ticket ticket = new Ticket();
        ParkingSpot spot = new ParkingSpot(2, ParkingType.CAR,false);
        ticket.setParkingSpot(spot);
        ticket.setVehicleRegNumber(licensePlateNumber);
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticketDAO.saveTicket(ticket);

        parkingService.processExitingVehicle();
        Date timeInDB = ticketDAO.getTicket(licensePlateNumber).getOutTime();
        double fare = ticketDAO.getTicket(licensePlateNumber).getPrice();

        assertNotNull(timeInDB);
        assertEquals(1.5, fare);
    }

    @Test
    public void testParkingLotExitWith5PercentDiscount() throws Exception {
        String licensePlateNumber = "ABCDEF";
        Ticket ticket = new Ticket();
        ParkingSpot spot = new ParkingSpot(4, ParkingType.BIKE,false);
        ticket.setParkingSpot(spot);
        ticket.setVehicleRegNumber(licensePlateNumber);
        ticket.setInTime(new Date(System.currentTimeMillis() - (3*60 * 60 * 1000)));
        ticketDAO.saveTicket(ticket);
        parkingService.processExitingVehicle();

        Ticket ticket2 = new Ticket();
        ParkingSpot spot2 = new ParkingSpot(4, ParkingType.BIKE,false);
        ticket2.setParkingSpot(spot2);
        ticket2.setVehicleRegNumber(licensePlateNumber);
        ticket2.setInTime(new Date(System.currentTimeMillis() - (5*60 * 60 * 1000)));
        ticketDAO.saveTicket(ticket2);
        parkingService.processExitingVehicle();

        double fare = ticketDAO.getTicket(licensePlateNumber).getPrice();

        assertEquals(4.75, fare);
    }

    //Done: check that the fare generated and out time are populated correctly in the database
    @Test
    public void testParkingACar() {
        String licensePlateNumber = "ABCDEF";
        when(inputReaderUtil.readSelection()).thenReturn(2);

        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(licensePlateNumber);

        assertNotNull(ticket);
        assertFalse(ticket.getParkingSpot().isAvailable());
        assertEquals(licensePlateNumber,ticket.getVehicleRegNumber());
    }
}
