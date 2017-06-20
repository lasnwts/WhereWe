package ru.nwts.wherewe.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by пользователь on 19.06.2017.
 */
@Entity(active = true)
public class GNodel {

    @Id
    private Long id;
    private String name;
    private int state;
    private int mode;
    private int rights;
    private long speed;
    private int moved;
    private long track_date;
    private long longtitude;
    private long lattitude;
    private String fbase_path;
    private String fbase_old;
    private int track_count;
    private int track_count_allowed;
    private String key;
    private String key_old;
    private int contact_id;
    private String email;
    private String part_email;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1313362356)
    private transient GNodelDao myDao;

    public GNodel() {
    }


    public GNodel(String name, int state, int mode, int rights, long speed, int moved,
                  long track_date, long longtitude, long lattitude, String fbase_path, String fbase_old, int track_count,
                  int track_count_allowed, String key, String key_old, int contact_id, String email, String part_email) {
        this.name = name;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        this.speed = speed;
        this.moved = moved;
        this.track_date = track_date;
        this.longtitude = longtitude;
        this.lattitude = lattitude;
        this.fbase_path = fbase_path;
        this.fbase_old = fbase_old;
        this.track_count = track_count;
        this.track_count_allowed = track_count_allowed;
        this.key = key;
        this.key_old = key_old;
        this.contact_id = contact_id;
        this.email = email;
        this.part_email = part_email;
    }


    @Generated(hash = 1860545194)
    public GNodel(Long id, String name, int state, int mode, int rights, long speed, int moved, long track_date,
            long longtitude, long lattitude, String fbase_path, String fbase_old, int track_count, int track_count_allowed,
            String key, String key_old, int contact_id, String email, String part_email) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        this.speed = speed;
        this.moved = moved;
        this.track_date = track_date;
        this.longtitude = longtitude;
        this.lattitude = lattitude;
        this.fbase_path = fbase_path;
        this.fbase_old = fbase_old;
        this.track_count = track_count;
        this.track_count_allowed = track_count_allowed;
        this.key = key;
        this.key_old = key_old;
        this.contact_id = contact_id;
        this.email = email;
        this.part_email = part_email;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getState() {
        return this.state;
    }


    public void setState(int state) {
        this.state = state;
    }


    public int getMode() {
        return this.mode;
    }


    public void setMode(int mode) {
        this.mode = mode;
    }


    public int getRights() {
        return this.rights;
    }


    public void setRights(int rights) {
        this.rights = rights;
    }


    public long getSpeed() {
        return this.speed;
    }


    public void setSpeed(long speed) {
        this.speed = speed;
    }


    public int getMoved() {
        return this.moved;
    }


    public void setMoved(int moved) {
        this.moved = moved;
    }


    public long getTrack_date() {
        return this.track_date;
    }


    public void setTrack_date(long track_date) {
        this.track_date = track_date;
    }


    public long getLongtitude() {
        return this.longtitude;
    }


    public void setLongtitude(long longtitude) {
        this.longtitude = longtitude;
    }


    public long getLattitude() {
        return this.lattitude;
    }


    public void setLattitude(long lattitude) {
        this.lattitude = lattitude;
    }


    public String getFbase_path() {
        return this.fbase_path;
    }


    public void setFbase_path(String fbase_path) {
        this.fbase_path = fbase_path;
    }


    public String getFbase_old() {
        return this.fbase_old;
    }


    public void setFbase_old(String fbase_old) {
        this.fbase_old = fbase_old;
    }


    public int getTrack_count() {
        return this.track_count;
    }


    public void setTrack_count(int track_count) {
        this.track_count = track_count;
    }


    public int getTrack_count_allowed() {
        return this.track_count_allowed;
    }


    public void setTrack_count_allowed(int track_count_allowed) {
        this.track_count_allowed = track_count_allowed;
    }


    public String getKey() {
        return this.key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getKey_old() {
        return this.key_old;
    }


    public void setKey_old(String key_old) {
        this.key_old = key_old;
    }


    public int getContact_id() {
        return this.contact_id;
    }


    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }


    public String getEmail() {
        return this.email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPart_email() {
        return this.part_email;
    }


    public void setPart_email(String part_email) {
        this.part_email = part_email;
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 125219950)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGNodelDao() : null;
    }
}
