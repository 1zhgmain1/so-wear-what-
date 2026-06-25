package com.example.weathercloth.v2.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDao_Impl implements AppDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserPreferenceEntity> __insertionAdapterOfUserPreferenceEntity;

  private final EntityInsertionAdapter<CityEntity> __insertionAdapterOfCityEntity;

  private final EntityInsertionAdapter<WardrobeItemEntity> __insertionAdapterOfWardrobeItemEntity;

  private final EntityInsertionAdapter<ReminderEntity> __insertionAdapterOfReminderEntity;

  private final EntityInsertionAdapter<WeatherCacheEntity> __insertionAdapterOfWeatherCacheEntity;

  private final EntityDeletionOrUpdateAdapter<CityEntity> __deletionAdapterOfCityEntity;

  private final EntityDeletionOrUpdateAdapter<WardrobeItemEntity> __deletionAdapterOfWardrobeItemEntity;

  private final EntityDeletionOrUpdateAdapter<ReminderEntity> __deletionAdapterOfReminderEntity;

  private final EntityDeletionOrUpdateAdapter<CityEntity> __updateAdapterOfCityEntity;

  private final SharedSQLiteStatement __preparedStmtOfSelectCity;

  public AppDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserPreferenceEntity = new EntityInsertionAdapter<UserPreferenceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `preferences` (`id`,`gender`,`style`,`thermalSensitivity`,`oftenBikes`,`likesHat`,`scene`,`reminderHour`,`reminderMinute`,`reminderEnabled`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserPreferenceEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getGender());
        statement.bindString(3, entity.getStyle());
        statement.bindLong(4, entity.getThermalSensitivity());
        final int _tmp = entity.getOftenBikes() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.getLikesHat() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindString(7, entity.getScene());
        statement.bindLong(8, entity.getReminderHour());
        statement.bindLong(9, entity.getReminderMinute());
        final int _tmp_2 = entity.getReminderEnabled() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
      }
    };
    this.__insertionAdapterOfCityEntity = new EntityInsertionAdapter<CityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `cities` (`id`,`name`,`latitude`,`longitude`,`selected`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CityEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        final int _tmp = entity.getSelected() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__insertionAdapterOfWardrobeItemEntity = new EntityInsertionAdapter<WardrobeItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `wardrobe` (`id`,`name`,`category`,`color`,`status`,`quantity`,`statusQuantity`,`warmth`,`waterproof`,`sunProtective`,`style`,`imageUri`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WardrobeItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCategory());
        if (entity.getColor() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getColor());
        }
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getQuantity());
        statement.bindLong(7, entity.getStatusQuantity());
        statement.bindLong(8, entity.getWarmth());
        final int _tmp = entity.getWaterproof() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.getSunProtective() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindString(11, entity.getStyle());
        if (entity.getImageUri() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getImageUri());
        }
      }
    };
    this.__insertionAdapterOfReminderEntity = new EntityInsertionAdapter<ReminderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reminders` (`id`,`hour`,`minute`,`enabled`,`createdAtMillis`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReminderEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getHour());
        statement.bindLong(3, entity.getMinute());
        final int _tmp = entity.getEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getCreatedAtMillis());
      }
    };
    this.__insertionAdapterOfWeatherCacheEntity = new EntityInsertionAdapter<WeatherCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `weather_cache` (`id`,`cityId`,`weatherJson`,`adviceJson`,`timestampMillis`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeatherCacheEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getCityId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCityId());
        }
        statement.bindString(3, entity.getWeatherJson());
        statement.bindString(4, entity.getAdviceJson());
        statement.bindLong(5, entity.getTimestampMillis());
      }
    };
    this.__deletionAdapterOfCityEntity = new EntityDeletionOrUpdateAdapter<CityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `cities` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CityEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfWardrobeItemEntity = new EntityDeletionOrUpdateAdapter<WardrobeItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `wardrobe` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WardrobeItemEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfReminderEntity = new EntityDeletionOrUpdateAdapter<ReminderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reminders` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReminderEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCityEntity = new EntityDeletionOrUpdateAdapter<CityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cities` SET `id` = ?,`name` = ?,`latitude` = ?,`longitude` = ?,`selected` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CityEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        final int _tmp = entity.getSelected() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfSelectCity = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE cities SET selected = CASE WHEN id = ? THEN 1 ELSE 0 END";
        return _query;
      }
    };
  }

  @Override
  public Object savePreference(final UserPreferenceEntity preference,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserPreferenceEntity.insert(preference);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object addCity(final CityEntity city, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCityEntity.insert(city);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object saveWardrobeItem(final WardrobeItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWardrobeItemEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object saveReminder(final ReminderEntity reminder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReminderEntity.insert(reminder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object saveWeatherCache(final WeatherCacheEntity cache,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWeatherCacheEntity.insert(cache);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCity(final CityEntity city, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCityEntity.handle(city);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWardrobeItem(final WardrobeItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWardrobeItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReminder(final ReminderEntity reminder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReminderEntity.handle(reminder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCity(final CityEntity city, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCityEntity.handle(city);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object selectCity(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSelectCity.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSelectCity.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserPreferenceEntity> observePreference() {
    final String _sql = "SELECT * FROM preferences WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"preferences"}, new Callable<UserPreferenceEntity>() {
      @Override
      @Nullable
      public UserPreferenceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "style");
          final int _cursorIndexOfThermalSensitivity = CursorUtil.getColumnIndexOrThrow(_cursor, "thermalSensitivity");
          final int _cursorIndexOfOftenBikes = CursorUtil.getColumnIndexOrThrow(_cursor, "oftenBikes");
          final int _cursorIndexOfLikesHat = CursorUtil.getColumnIndexOrThrow(_cursor, "likesHat");
          final int _cursorIndexOfScene = CursorUtil.getColumnIndexOrThrow(_cursor, "scene");
          final int _cursorIndexOfReminderHour = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderHour");
          final int _cursorIndexOfReminderMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinute");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderEnabled");
          final UserPreferenceEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpGender;
            _tmpGender = _cursor.getString(_cursorIndexOfGender);
            final String _tmpStyle;
            _tmpStyle = _cursor.getString(_cursorIndexOfStyle);
            final int _tmpThermalSensitivity;
            _tmpThermalSensitivity = _cursor.getInt(_cursorIndexOfThermalSensitivity);
            final boolean _tmpOftenBikes;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOftenBikes);
            _tmpOftenBikes = _tmp != 0;
            final boolean _tmpLikesHat;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfLikesHat);
            _tmpLikesHat = _tmp_1 != 0;
            final String _tmpScene;
            _tmpScene = _cursor.getString(_cursorIndexOfScene);
            final int _tmpReminderHour;
            _tmpReminderHour = _cursor.getInt(_cursorIndexOfReminderHour);
            final int _tmpReminderMinute;
            _tmpReminderMinute = _cursor.getInt(_cursorIndexOfReminderMinute);
            final boolean _tmpReminderEnabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp_2 != 0;
            _result = new UserPreferenceEntity(_tmpId,_tmpGender,_tmpStyle,_tmpThermalSensitivity,_tmpOftenBikes,_tmpLikesHat,_tmpScene,_tmpReminderHour,_tmpReminderMinute,_tmpReminderEnabled);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CityEntity>> observeCities() {
    final String _sql = "SELECT * FROM cities ORDER BY selected DESC, name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cities"}, new Callable<List<CityEntity>>() {
      @Override
      @NonNull
      public List<CityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSelected = CursorUtil.getColumnIndexOrThrow(_cursor, "selected");
          final List<CityEntity> _result = new ArrayList<CityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CityEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final boolean _tmpSelected;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSelected);
            _tmpSelected = _tmp != 0;
            _item = new CityEntity(_tmpId,_tmpName,_tmpLatitude,_tmpLongitude,_tmpSelected);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<WardrobeItemEntity>> observeWardrobe() {
    final String _sql = "SELECT * FROM wardrobe ORDER BY category, name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"wardrobe"}, new Callable<List<WardrobeItemEntity>>() {
      @Override
      @NonNull
      public List<WardrobeItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfStatusQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "statusQuantity");
          final int _cursorIndexOfWarmth = CursorUtil.getColumnIndexOrThrow(_cursor, "warmth");
          final int _cursorIndexOfWaterproof = CursorUtil.getColumnIndexOrThrow(_cursor, "waterproof");
          final int _cursorIndexOfSunProtective = CursorUtil.getColumnIndexOrThrow(_cursor, "sunProtective");
          final int _cursorIndexOfStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "style");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final List<WardrobeItemEntity> _result = new ArrayList<WardrobeItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WardrobeItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final int _tmpStatusQuantity;
            _tmpStatusQuantity = _cursor.getInt(_cursorIndexOfStatusQuantity);
            final int _tmpWarmth;
            _tmpWarmth = _cursor.getInt(_cursorIndexOfWarmth);
            final boolean _tmpWaterproof;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWaterproof);
            _tmpWaterproof = _tmp != 0;
            final boolean _tmpSunProtective;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSunProtective);
            _tmpSunProtective = _tmp_1 != 0;
            final String _tmpStyle;
            _tmpStyle = _cursor.getString(_cursorIndexOfStyle);
            final String _tmpImageUri;
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _tmpImageUri = null;
            } else {
              _tmpImageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            _item = new WardrobeItemEntity(_tmpId,_tmpName,_tmpCategory,_tmpColor,_tmpStatus,_tmpQuantity,_tmpStatusQuantity,_tmpWarmth,_tmpWaterproof,_tmpSunProtective,_tmpStyle,_tmpImageUri);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ReminderEntity>> observeReminders() {
    final String _sql = "SELECT * FROM reminders ORDER BY createdAtMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reminders"}, new Callable<List<ReminderEntity>>() {
      @Override
      @NonNull
      public List<ReminderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHour = CursorUtil.getColumnIndexOrThrow(_cursor, "hour");
          final int _cursorIndexOfMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "minute");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final List<ReminderEntity> _result = new ArrayList<ReminderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReminderEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpHour;
            _tmpHour = _cursor.getInt(_cursorIndexOfHour);
            final int _tmpMinute;
            _tmpMinute = _cursor.getInt(_cursorIndexOfMinute);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            _item = new ReminderEntity(_tmpId,_tmpHour,_tmpMinute,_tmpEnabled,_tmpCreatedAtMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getWeatherCache(final Continuation<? super WeatherCacheEntity> $completion) {
    final String _sql = "SELECT * FROM weather_cache WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WeatherCacheEntity>() {
      @Override
      @Nullable
      public WeatherCacheEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCityId = CursorUtil.getColumnIndexOrThrow(_cursor, "cityId");
          final int _cursorIndexOfWeatherJson = CursorUtil.getColumnIndexOrThrow(_cursor, "weatherJson");
          final int _cursorIndexOfAdviceJson = CursorUtil.getColumnIndexOrThrow(_cursor, "adviceJson");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final WeatherCacheEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Long _tmpCityId;
            if (_cursor.isNull(_cursorIndexOfCityId)) {
              _tmpCityId = null;
            } else {
              _tmpCityId = _cursor.getLong(_cursorIndexOfCityId);
            }
            final String _tmpWeatherJson;
            _tmpWeatherJson = _cursor.getString(_cursorIndexOfWeatherJson);
            final String _tmpAdviceJson;
            _tmpAdviceJson = _cursor.getString(_cursorIndexOfAdviceJson);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            _result = new WeatherCacheEntity(_tmpId,_tmpCityId,_tmpWeatherJson,_tmpAdviceJson,_tmpTimestampMillis);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
