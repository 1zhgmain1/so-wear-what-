package com.example.weathercloth.v2.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AppDao _appDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(7) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `preferences` (`id` INTEGER NOT NULL, `gender` TEXT NOT NULL, `style` TEXT NOT NULL, `thermalSensitivity` INTEGER NOT NULL, `oftenBikes` INTEGER NOT NULL, `likesHat` INTEGER NOT NULL, `scene` TEXT NOT NULL, `reminderHour` INTEGER NOT NULL, `reminderMinute` INTEGER NOT NULL, `reminderEnabled` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `cities` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `selected` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `wardrobe` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `color` TEXT, `status` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `statusQuantity` INTEGER NOT NULL, `warmth` INTEGER NOT NULL, `waterproof` INTEGER NOT NULL, `sunProtective` INTEGER NOT NULL, `style` TEXT NOT NULL, `imageUri` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `enabled` INTEGER NOT NULL, `createdAtMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `weather_cache` (`id` INTEGER NOT NULL, `cityId` INTEGER, `weatherJson` TEXT NOT NULL, `adviceJson` TEXT NOT NULL, `timestampMillis` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f472fa4e2ad60a5cd44104d79e13c98f')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `preferences`");
        db.execSQL("DROP TABLE IF EXISTS `cities`");
        db.execSQL("DROP TABLE IF EXISTS `wardrobe`");
        db.execSQL("DROP TABLE IF EXISTS `reminders`");
        db.execSQL("DROP TABLE IF EXISTS `weather_cache`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPreferences = new HashMap<String, TableInfo.Column>(10);
        _columnsPreferences.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("gender", new TableInfo.Column("gender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("style", new TableInfo.Column("style", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("thermalSensitivity", new TableInfo.Column("thermalSensitivity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("oftenBikes", new TableInfo.Column("oftenBikes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("likesHat", new TableInfo.Column("likesHat", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("scene", new TableInfo.Column("scene", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("reminderHour", new TableInfo.Column("reminderHour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("reminderMinute", new TableInfo.Column("reminderMinute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreferences.put("reminderEnabled", new TableInfo.Column("reminderEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPreferences = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPreferences = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPreferences = new TableInfo("preferences", _columnsPreferences, _foreignKeysPreferences, _indicesPreferences);
        final TableInfo _existingPreferences = TableInfo.read(db, "preferences");
        if (!_infoPreferences.equals(_existingPreferences)) {
          return new RoomOpenHelper.ValidationResult(false, "preferences(com.example.weathercloth.v2.data.local.UserPreferenceEntity).\n"
                  + " Expected:\n" + _infoPreferences + "\n"
                  + " Found:\n" + _existingPreferences);
        }
        final HashMap<String, TableInfo.Column> _columnsCities = new HashMap<String, TableInfo.Column>(5);
        _columnsCities.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCities.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCities.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCities.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCities.put("selected", new TableInfo.Column("selected", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCities = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCities = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCities = new TableInfo("cities", _columnsCities, _foreignKeysCities, _indicesCities);
        final TableInfo _existingCities = TableInfo.read(db, "cities");
        if (!_infoCities.equals(_existingCities)) {
          return new RoomOpenHelper.ValidationResult(false, "cities(com.example.weathercloth.v2.data.local.CityEntity).\n"
                  + " Expected:\n" + _infoCities + "\n"
                  + " Found:\n" + _existingCities);
        }
        final HashMap<String, TableInfo.Column> _columnsWardrobe = new HashMap<String, TableInfo.Column>(12);
        _columnsWardrobe.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("color", new TableInfo.Column("color", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("statusQuantity", new TableInfo.Column("statusQuantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("warmth", new TableInfo.Column("warmth", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("waterproof", new TableInfo.Column("waterproof", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("sunProtective", new TableInfo.Column("sunProtective", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("style", new TableInfo.Column("style", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWardrobe.put("imageUri", new TableInfo.Column("imageUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWardrobe = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWardrobe = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWardrobe = new TableInfo("wardrobe", _columnsWardrobe, _foreignKeysWardrobe, _indicesWardrobe);
        final TableInfo _existingWardrobe = TableInfo.read(db, "wardrobe");
        if (!_infoWardrobe.equals(_existingWardrobe)) {
          return new RoomOpenHelper.ValidationResult(false, "wardrobe(com.example.weathercloth.v2.data.local.WardrobeItemEntity).\n"
                  + " Expected:\n" + _infoWardrobe + "\n"
                  + " Found:\n" + _existingWardrobe);
        }
        final HashMap<String, TableInfo.Column> _columnsReminders = new HashMap<String, TableInfo.Column>(5);
        _columnsReminders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("hour", new TableInfo.Column("hour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("minute", new TableInfo.Column("minute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("createdAtMillis", new TableInfo.Column("createdAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReminders = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReminders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReminders = new TableInfo("reminders", _columnsReminders, _foreignKeysReminders, _indicesReminders);
        final TableInfo _existingReminders = TableInfo.read(db, "reminders");
        if (!_infoReminders.equals(_existingReminders)) {
          return new RoomOpenHelper.ValidationResult(false, "reminders(com.example.weathercloth.v2.data.local.ReminderEntity).\n"
                  + " Expected:\n" + _infoReminders + "\n"
                  + " Found:\n" + _existingReminders);
        }
        final HashMap<String, TableInfo.Column> _columnsWeatherCache = new HashMap<String, TableInfo.Column>(5);
        _columnsWeatherCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("cityId", new TableInfo.Column("cityId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("weatherJson", new TableInfo.Column("weatherJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("adviceJson", new TableInfo.Column("adviceJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("timestampMillis", new TableInfo.Column("timestampMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWeatherCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWeatherCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWeatherCache = new TableInfo("weather_cache", _columnsWeatherCache, _foreignKeysWeatherCache, _indicesWeatherCache);
        final TableInfo _existingWeatherCache = TableInfo.read(db, "weather_cache");
        if (!_infoWeatherCache.equals(_existingWeatherCache)) {
          return new RoomOpenHelper.ValidationResult(false, "weather_cache(com.example.weathercloth.v2.data.local.WeatherCacheEntity).\n"
                  + " Expected:\n" + _infoWeatherCache + "\n"
                  + " Found:\n" + _existingWeatherCache);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f472fa4e2ad60a5cd44104d79e13c98f", "b1262ad967819dafdc78ac316a9a436a");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "preferences","cities","wardrobe","reminders","weather_cache");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `preferences`");
      _db.execSQL("DELETE FROM `cities`");
      _db.execSQL("DELETE FROM `wardrobe`");
      _db.execSQL("DELETE FROM `reminders`");
      _db.execSQL("DELETE FROM `weather_cache`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AppDao.class, AppDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AppDao dao() {
    if (_appDao != null) {
      return _appDao;
    } else {
      synchronized(this) {
        if(_appDao == null) {
          _appDao = new AppDao_Impl(this);
        }
        return _appDao;
      }
    }
  }
}
