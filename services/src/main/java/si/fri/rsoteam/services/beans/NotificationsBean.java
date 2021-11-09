package si.fri.rsoteam.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.logging.Logger;

@RequestScoped
public class NotificationsBean {
    private Logger log = Logger.getLogger(NotificationsBean.class.getName());

    @Inject
    private EntityManager em;

    // TODO Implement CRUD and other required methods

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
