package vtechphone.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class VTechPhonePU {
    public static EntityManager getEntityManager() {
        var factory = Persistence.createEntityManagerFactory("VTechPhonePU");
        return factory.createEntityManager();
    }
}