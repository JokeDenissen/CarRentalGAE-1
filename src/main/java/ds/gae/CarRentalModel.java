package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;
 
public class CarRentalModel {
	
	public Map<String,CarRentalCompany> CRCS = new HashMap<String, CarRentalCompany>();	
	
	private static CarRentalModel instance;
	
	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}
		
	/**
	 * Get the car types available in the given car rental company.
	 *
	 * @param 	crcName
	 * 			the car rental company
	 * @return	The list of car types (i.e. name of car type), available
	 * 			in the given car rental company.
	 */
	public Set<String> getCarTypesNames(String crcName) {
		Set<String> out = new HashSet<String>();
		
		EntityManager manager = EMF.get().createEntityManager();
		try {
			out = new HashSet<String>(manager.createNamedQuery("getCarTypeNamesOfCompany").setParameter("name", crcName).getResultList());
		}
		finally {manager.close();}
    	
    	return null;
	}

    /**
     * Get all registered car rental companies
     *
     * @return	the list of car rental companies
     */
    public Collection<String> getAllRentalCompanyNames() {
    	List<String> out = new ArrayList<String>();
		
		EntityManager manager = EMF.get().createEntityManager();
		try {
			out = manager.createNamedQuery("getAllCompanyNames").getResultList();
		}
		finally {manager.close();}
    	
    	return out;
    }
	
	/**
	 * Create a quote according to the given reservation constraints (tentative reservation).
	 * 
	 * @param	company
	 * 			name of the car renter company
	 * @param	renterName 
	 * 			name of the car renter 
	 * @param 	constraints
	 * 			reservation constraints for the quote
	 * @return	The newly created quote.
	 *  
	 * @throws ReservationException
	 * 			No car available that fits the given constraints.
	 */
    public Quote createQuote(String company, String renterName, ReservationConstraints constraints) throws ReservationException {
    	EntityManager manager = EMF.get().createEntityManager();
    	Quote out = null;
    	try {
    		CarRentalCompany crc = manager.find(CarRentalCompany.class, company);
            if (crc != null) {
                out = crc.createQuote(constraints, renterName);
            } else {
            	throw new ReservationException("CarRentalCompany not found.");    	
            }
    	}
    	finally {manager.close();}
        
        return out;
    }
    
	/**
	 * Confirm the given quote.
	 *
	 * @param 	q
	 * 			Quote to confirm
	 * 
	 * @throws ReservationException
	 * 			Confirmation of given quote failed.	
	 */
	public void confirmQuote(Quote q) throws ReservationException {
		EntityManager manager = EMF.get().createEntityManager();
		try {
			CarRentalCompany crc = manager.find(CarRentalCompany.class, q.getRentalCompany());
	        crc.confirmQuote(q);
		}
		finally {manager.close();}
	}
	
    /**
	 * Confirm the given list of quotes
	 * 
	 * @param 	quotes 
	 * 			the quotes to confirm
	 * @return	The list of reservations, resulting from confirming all given quotes.
	 * 
	 * @throws 	ReservationException
	 * 			One of the quotes cannot be confirmed. 
	 * 			Therefore none of the given quotes is confirmed.
	 */
    public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {    	
		// TODO add implementation
    	return null;
    }
	
	/**
	 * Get all reservations made by the given car renter.
	 *
	 * @param 	renter
	 * 			name of the car renter
	 * @return	the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		List<Reservation> out = new ArrayList<Reservation>();
		
		EntityManager manager = EMF.get().createEntityManager();
		try {
			out = manager.createNamedQuery("getAllReservationsByRenter").setParameter("renter", renter).getResultList();
		}
		finally {manager.close();}
    	
    	return out;
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param 	crcName
     * 			the given car rental company
     * @return	The list of car types in the given car rental company.
     */
    public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
    	Set<CarType> out = new HashSet<CarType>();
		
		EntityManager manager = EMF.get().createEntityManager();
		try {
			out = (Set<CarType>)(manager.createNamedQuery("getCarTypesOfCompany").setParameter("name", crcName).getResultList().get(0));
		}
		finally {manager.close();}
    	
    	return out;
    }
	
    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A list of car IDs of cars with the given car type.
     */
    public Collection<Integer> getCarIdsByCarType(String crcName, CarType carType) {
Set<Integer> out = new HashSet<Integer>();
		
		EntityManager manager = EMF.get().createEntityManager();
		try {
			Set<Car> cars = (Set<Car>)(manager.createNamedQuery("getCarsOfCompany").setParameter("name", crcName).getResultList().get(0));
			for (Car car : cars) {
				CarType type = (CarType)(manager.createNamedQuery("getTypeOfId").setParameter("id", car.getType()).getResultList().get(0));
				if (type.getName().equals(carType)) {out.add(car.getId());}
			}
		}
		finally {manager.close();}
    	
    	return out;
    }
    
    /**
     * Get the amount of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A number, representing the amount of cars of the given car type.
     */
    public int getAmountOfCarsByCarType(String crcName, CarType carType) {
    	return this.getCarsByCarType(crcName, carType).size();
    }

	/**
	 * Get the list of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	List of cars of the given car type
	 */
	private List<Car> getCarsByCarType(String crcName, CarType carType) {				
		// FIXME: use persistence instead

		List<Car> out = new ArrayList<Car>(); 
		for(CarRentalCompany crc : CRCS.values()) {
			for (Car c : crc.getCars()) {
//				if (c.getType() == carType) { 
//					out.add(c);
//				}
			}
		}
		return out;
	}

	/**
	 * Check whether the given car renter has reservations.
	 *
	 * @param 	renter
	 * 			the car renter
	 * @return	True if the number of reservations of the given car renter is higher than 0.
	 * 			False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;		
	}	
}