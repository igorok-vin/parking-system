package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

    private static TicketDAO ticketDAO;
    private static Ticket ticket;
    private static final String LICENSE_PLATE_NUMBER = "ABCDEF";
    private static DataBaseTestConfig dataBaseConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
    @Mock
    private static  Logger loggerMock = LogManager.getLogger("TicketDAO");

   @Mock
   private static DataBaseTestConfig dataBaseConfigMock = new DataBaseTestConfig();

    @BeforeEach
    private void setUp() throws Exception {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseConfig ;
        ticketDAO.setLogger(loggerMock);
        dataBasePrepareService.clearDataBaseEntries();

        ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(LICENSE_PLATE_NUMBER);
        ticket.setPrice(35);
    }


    @Test
    public void savedTicketTestShouldReturnOwnValues(){

        assertEquals(35.0 , ticket.getPrice());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals(ParkingType.CAR, ticket.getParkingSpot().getParkingType());
        assertEquals(1, ticket.getParkingSpot().getId());
        assertEquals(false, ticket.getParkingSpot().isAvailable());
    }

    @Test
    public void updateTicketTestShouldReturnDifferentValues(){
        ticket.setOutTime(new Date());
        boolean testMethod = ticketDAO.updateTicket(ticket);
        assertTrue(testMethod);
    }
}
