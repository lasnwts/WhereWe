package ru.nwts.wherewe.database;

/**
 * Created by Надя on 24.12.2016.
 */

public interface DBConstant {

    //LOG
    public static final String TAG = "MyLogs";

    //TableName
    /**
     * users        -   таблица пользователей
     * groups       -   таблица групп
     * links        -   связь пользователей и групп
     * tracks       -   таблица трека пути
     * statistics   -   таблица статистики
     */
    public static final String TABLE_USERS = "users";
    public static final String TABLE_GROUPS = "groups";
    public static final String TABLE_LINKS = "links";
    public static final String TABLE_TRACKS = "tracks";
    public static final String TABLE_STATISTICS = "statistics";

    //Column Name from all tables
    public static final String KEY_ID = "_id";
    public static final String KEY_GROUP_ID = "group_id";
    public static final String KEY_TRACK_ID = "_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_DATE = "track_date";
    public static final String KEY_LONGTITUDE = "longtitude";
    public static final String KEY_LATTITUDE = "lattitude";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_MOVED = "moved";
    public static final String KEY_STATE = "state";
    public static final String KEY_MODE = "mode";
    public static final String KEY_RIGHTS = "rights";
    public static final String KEY_FBASE_PATH = "fbase_path";
    public static final String KEY_FBASE_OLD = "fbase_old";
    public static final String KEY_TRACK_COUNT = "track_count";
    public static final String KEY_TRACK_COUNT_ALLOWED = "track_count_allowed";
    public static final String KEY_CONTACT_ID = "contact_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ENCRYPTION = "key";
    public static final String KEY_ENCRYPTION_OLD = "key_old";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PART_EMAIL = "part_email";


    //Mode
    /**
     * Mode enumeration (режим состояния):
     * Номер цифровой	Имя константы	Описание
     * 0	NON_MODE	Неопределен
     * 1	NORMAL	Нормальный режим
     * 2	ALARM	Режим тревоги
     */

    public static final int MODE_NON_MODE = 0;
    public static final int MODE_NORMAL = 1;
    public static final int MODE_ALARM = 2;

    /**
     * Mode enumeration (тип группы):
     * 0 – default групп, сюда попадают все, кто не имеет группы
     * 1 – temporary временная группа
     * 2 – пользовательская группа, ее можно редактировать, удалять.
     */

    public static final int GROUP_MODE_DEFAULT = 0;
    public static final int GROUP_MODE_TEMP = 1;
    public static final int GROUP_MODE_USER = 2;

    //Moved
    /**
     * Тип движения (перечисление)
     * Стояние на месте Хотьба Поездка и т.п.
     * 0    -   Стояние
     * 1    -   Хотьба
     * 2    -   Бег
     * 3    -   Езда
     * 4    -   Не определен
     */

    public static final int MOVE_STANDING = 0;
    public static final int MOVE_WALKING = 1;
    public static final int MOVE_RUNNING = 2;
    public static final int MOVE_DRIVING = 3;
    public static final int MOVE_UNDETERMINED = 4;

    //Rights
    /**
     * Rights enumeration (права пользователя):
     * Номер цифровой	Имя константы	Описание
     * 0	NORMAL	Нормальный режим
     * 1	AUTO	Датчик, авто
     * 2	CHILD	Ребенок
     */

    public static final int RIGHTS_NORMAL = 0;
    public static final int RIGHTS_AUTO = 1;
    public static final int RIGHTS_CHILD = 2;

    //Status
    /**
     * Status enumeration (Статус соединения):
     * Номер цифровой	Имя константы	Описание
     * 0	CONNECT	Отправлен запрос на подключение, ожидание подтверждения
     * 1	ACTIVED	Подключен, информация обновляется
     * 2	NON_ACTIVED	Информация не обновляется
     * 3	DELETE	Удален
     */

    public static final String STATUS = "status";

    public static final int STATE_CONNECT = 0;
    public static final int STATE_ACTIVED = 1;
    public static final int STATE_NON_ACTIVED = 2;
    public static final int STATE_DELETE = 3;


}
