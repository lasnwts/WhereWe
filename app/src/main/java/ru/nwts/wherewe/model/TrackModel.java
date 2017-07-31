package ru.nwts.wherewe.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by пользователь on 21.06.2017.
 */
@Entity(active = true)
public class TrackModel {
    @Id
    private Long id;
    private String name;
    private int state;
    private int mode;
    private int rights;
    private double speed;
    private int moved;
    private long track_date;
    private double longtitude;
    private double lattitude;
    private int contact_id;
    private String email;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1457393434)
    private transient TrackModelDao myDao;

    public TrackModel( double lattitude, double longtitude, double speed, int moved, int state,
                       int mode, int rights, String email, String name, long track_date,  int contact_id) {

        this.name = name;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        this.speed = speed;
        this.moved = moved;
        this.track_date = track_date;
        this.longtitude = longtitude;
        this.lattitude = lattitude;
        this.contact_id = contact_id;
        this.email = email;
    }

    @Generated(hash = 942854375)
    public TrackModel(Long id, String name, int state, int mode, int rights, double speed, int moved, long track_date,
            double longtitude, double lattitude, int contact_id, String email) {
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
        this.contact_id = contact_id;
        this.email = email;
    }

    @Generated(hash = 1083798577)
    public TrackModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getRights() {
        return rights;
    }

    public void setRights(int rights) {
        this.rights = rights;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getMoved() {
        return moved;
    }

    public void setMoved(int moved) {
        this.moved = moved;
    }

    public long getTrack_date() {
        return track_date;
    }

    public void setTrack_date(long track_date) {
        this.track_date = track_date;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1905747753)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTrackModelDao() : null;
    }
}
