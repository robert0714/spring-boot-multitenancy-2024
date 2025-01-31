package com.thomasvitale.instrumentservice.instrument;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.descriptor.java.UUIDJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * 系統操作紀錄
 */
@Setter
@Getter
@Entity
@Table(name = "request_records")
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "系統操作紀錄")
public class RequestRecords implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 序號 primary key
     */
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false ,columnDefinition="varchar(36)")
    @JdbcType(VarcharJdbcType.class)
    @JavaType(UUIDJavaType.class) 
    private UUID id;

    /**
     * 執行的 API 名稱
     */
    @Column(name = "api_name")
    @Schema(description = "執行的 API 名稱")
    private String apiName;

    /**
     * 執行者名稱
     */
    @Column(name = "user_name", nullable = false)
    @NotNull(message = "userName is required")
    @Schema(description = "執行者名稱")
    private String userName;

    /**
     * 執行者公司統編
     */
    @Column(name = "realmname", nullable = false)
    @NotNull(message = "realmname is required")
    @Schema(description = "執行者公司統編")
    private String realmname;

    /**
     * 執行者 IP
     */
    @Column(name = "user_ip", nullable = false)
    @NotNull(message = "userIp is required")
    @Schema(description = "執行者 IP")
    private String userIp;

    /**
     * 執行的 HTTP request method
     */
    @Column(name = "request_method", nullable = false)
    @NotNull(message = "requestMethod is required")
    @Schema(description = "執行的 HTTP request method")
    private String requestMethod;

    /**
     * 執行的 HTTP request URL
     */
    @Column(name = "request_url", nullable = false)
    @NotNull(message = "requestUrl is required")
    @Schema(description = "執行的 HTTP request URL")
    private String requestUrl;

    /**
     * 執行的 HTTP request 參數值
     */
    @Column(name = "request_param")
    @Schema(description = "執行的 HTTP request 參數值")
    private String requestParam;

    /**
     * 執行後的 HTTP Response
     */
    @Column(name = "response_body" , columnDefinition="TEXT")
    @Schema(description = "執行後的 HTTP Response")
    private String responseBody;

    /**
     * 執行回傳的 Status
     */
    @Column(name = "status", nullable = false)
    @NotNull(message = "status is required")
    @Schema(description = "執行回傳的 Status")
    private Integer status;

    /**
     * 執行後回傳的錯誤訊息
     */
    @Column(name = "error_msg")
    @Schema(description = "執行後回傳的錯誤訊息")
    private String errorMsg;

    /**
     * 執行的時間
     */
    @Column(name = "request_time", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "requestTime is required")
    @Schema(description = "執行的時間")
    private OffsetDateTime requestTime;

    /**
     * 執行花費時間
     */
    @Column(name = "cost_time", nullable = false)
    @NotNull(message = "costTime is required")
    @Schema(description = "執行花費時間")
    private Long costTime;
}
