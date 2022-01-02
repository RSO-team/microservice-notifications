package si.fri.rsoteam.services.beans;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.fri.rsoteam.dtos.NotificationLogDto;
import si.fri.rsoteam.models.entities.NotificationLogEntity;
import si.fri.rsoteam.services.mappers.NotificationLogMapper;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class NotificationLogBean {
    private Logger log = LogManager.getLogger(NotificationLogBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    public List<NotificationLogDto> getList() {
        TypedQuery<NotificationLogEntity> query = em.createNamedQuery("NotificationLog.getAll", NotificationLogEntity.class);
        return query.getResultList().stream().map(NotificationLogMapper::entityToDto).collect(Collectors.toList());
    }

    public NotificationLogDto get(Integer id) {
        NotificationLogEntity entity = em.find(NotificationLogEntity.class, id);
        return NotificationLogMapper.entityToDto(entity);
    }

    public NotificationLogDto create(NotificationLogDto user) {
        this.beginTx();
        NotificationLogEntity entity = NotificationLogMapper.dtoToEntity(user);
        em.persist(entity);
        this.commitTx();
        return NotificationLogMapper.entityToDto(entity);
    }

    public NotificationLogDto update(NotificationLogDto user, Integer id){
        this.beginTx();

        NotificationLogEntity entity = em.find(NotificationLogEntity.class, id);
        entity.setReceiver(user.receiver);
        entity.setSender(user.sender);
        entity.setTimestamp(user.timestamp);
        entity.setSentAt(user.sentAt);
        entity.setContent(user.content);
        em.persist(entity);

        this.commitTx();

        return NotificationLogMapper.entityToDto(entity);
    }

    public void delete(Integer id) {
        NotificationLogDto entity = em.find(NotificationLogDto.class, id);
        if (entity != null) {
            this.beginTx();
            em.remove(entity);
            this.commitTx();
        } else {
            throw new NotFoundException("Obj not found");
        }
    }

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
