package edu.neu.myapplication5_25.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ScheduleEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String title;
    private String content;
    private String date;
    private String time;
    private boolean isNotified;
    private long createdAt;
    private long updatedAt;

    public ScheduleEntry() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isNotified = false;
    }

    public ScheduleEntry(String title, String content, String date, String time) {
        this();
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取完整的日期时间字符串
     * @return 格式化的日期时间
     */
    public String getFormattedDateTime() {
        if (date == null || time == null) {
            return "未设置时间";
        }
        return date + " " + time;
    }

    /**
     * 获取格式化的日期
     * @return 格式化的日期字符串
     */
    public String getFormattedDate() {
        if (date == null) {
            return "未设置日期";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
        }
        
        return date;
    }

    /**
     * 获取格式化的时间
     * @return 格式化的时间字符串
     */
    public String getFormattedTime() {
        if (time == null) {
            return "未设置时间";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date parsedTime = inputFormat.parse(time);
            if (parsedTime != null) {
                return outputFormat.format(parsedTime);
            }
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
        }
        
        return time;
    }

    /**
     * 检查日程是否已过期
     * @return 是否已过期
     */
    public boolean isExpired() {
        if (date == null || time == null) {
            return false;
        }
        
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date scheduleDate = format.parse(date + " " + time);
            if (scheduleDate != null) {
                return scheduleDate.before(new Date());
            }
        } catch (Exception e) {
            // 解析失败，认为未过期
        }
        
        return false;
    }

    /**
     * 获取距离日程的时间差（分钟）
     * @return 时间差，负数表示已过期
     */
    public long getTimeUntilSchedule() {
        if (date == null || time == null) {
            return Long.MAX_VALUE;
        }
        
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date scheduleDate = format.parse(date + " " + time);
            if (scheduleDate != null) {
                long diff = scheduleDate.getTime() - System.currentTimeMillis();
                return diff / (1000 * 60); // 转换为分钟
            }
        } catch (Exception e) {
            // 解析失败
        }
        
        return Long.MAX_VALUE;
    }

    /**
     * 获取日程状态描述
     * @return 状态描述
     */
    public String getStatusDescription() {
        if (isExpired()) {
            return "已过期";
        } else {
            long minutesUntil = getTimeUntilSchedule();
            if (minutesUntil < 60) {
                return "即将开始";
            } else if (minutesUntil < 24 * 60) {
                return "今日";
            } else if (minutesUntil < 7 * 24 * 60) {
                return "本周";
            } else {
                return "未来";
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleEntry that = (ScheduleEntry) o;
        return id == that.id &&
                isNotified == that.isNotified &&
                createdAt == that.createdAt &&
                updatedAt == that.updatedAt &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(date, that.date) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, date, time, isNotified, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", isNotified=" + isNotified +
                '}';
    }
} 