package vtechphone.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class VTechPhonePU {
    private static volatile EntityManagerFactory factory;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(VTechPhonePU::closeFactory));
    }

    private static EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            synchronized (VTechPhonePU.class) {
                if (factory == null || !factory.isOpen()) {
                    factory = Persistence.createEntityManagerFactory("VTechPhonePU");
                }
            }
        }
        return factory;
    }

    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    public static synchronized void closeFactory() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            factory = null;
        }
    }
}