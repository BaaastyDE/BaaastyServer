package de.baaasty.baaastyserver.database.dao.user;

import de.baaasty.baaastyserver.database.dao.User;
import de.chojo.sadu.base.QueryFactory;

import java.sql.Timestamp;
import java.time.Instant;

public class Meta extends QueryFactory {
    private final User user;
    private String language = null;
    private Long onlineTime = null;
    private Timestamp lastSeen = null;
    private Timestamp firstJoin = null;

    public Meta(User user) {
        super(user);
        this.user = user;
    }

    public String language() {
        if (language == null) {
            this.language = builder(String.class)
                    .query("""
                            SELECT
                                language
                            FROM
                                user_meta
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getString("language"))
                    .firstSync()
                    .orElse("DE");
        }

        return language;
    }

    public boolean language(String language) {
        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            language
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                            language = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setString(language)
                        .setString(language)
                )
                .update()
                .sendSync()
                .changed();

        if (changed)
            this.language = language;

        return changed;
    }

    public Long onlineTime() {
        if (onlineTime == null) {
            this.onlineTime = builder(Long.class)
                    .query("""
                            SELECT
                                online_time
                            FROM
                                user_meta
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getLong("online_time"))
                    .firstSync()
                    .orElse(0L);
        }

        return onlineTime;
    }

    public boolean onlineTime(long onlineTime) {
        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            online_time
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        online_time = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setLong(onlineTime)
                        .setLong(onlineTime)
                )
                .update()
                .sendSync()
                .changed();

        if (changed)
            this.onlineTime = onlineTime;

        return changed;
    }

    public Timestamp lastSeen() {
        if (lastSeen == null) {
            this.lastSeen = builder(Timestamp.class)
                    .query("""
                            SELECT
                                last_seen
                            FROM
                                user_meta
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getTimestamp("last_seen"))
                    .firstSync()
                    .orElse(Timestamp.from(Instant.now()));
        }

        return lastSeen;
    }

    public boolean updateLastSeen() {
        Timestamp now = Timestamp.from(Instant.now());

        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            last_seen
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        last_seen = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setTimestamp(now)
                        .setTimestamp(now)
                )
                .update()
                .sendSync()
                .changed();

        if (changed)
            this.lastSeen = now;

        return changed;
    }

    public Timestamp firstJoin() {
        if (firstJoin == null) {
            this.firstJoin = builder(Timestamp.class)
                    .query("""
                        SELECT
                            create_date
                        FROM
                            user_meta
                        WHERE
                            user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getTimestamp("create_date"))
                    .firstSync()
                    .orElse(Timestamp.from(Instant.now()));
        }

        return firstJoin;
    }

    public boolean setFirstJoin() {
        Timestamp now = Timestamp.from(Instant.now());

        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            create_date
                        ) VALUES (
                            ?,
                            ?
                        )""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setTimestamp(now)
                )
                .update()
                .sendSync()
                .changed();

        if (changed)
            this.firstJoin = now;

        return changed;
    }
}
