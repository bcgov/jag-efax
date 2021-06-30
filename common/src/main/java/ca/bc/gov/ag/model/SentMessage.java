package ca.bc.gov.ag.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("sentmessage")
public class SentMessage {

    @Id
    private String uuid;
    private String jobId;
    private Date createdTs;

    public SentMessage() {
    }

    public SentMessage(String uuid, String jobId, Date createdTs) {
        super();
        this.uuid = uuid;
        this.jobId = jobId;
        this.createdTs = createdTs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Date getCreatedTs() {
        return createdTs;
    }
    
    public void setCreatedTs(Date createdTs) {
        this.createdTs = createdTs;
    }

}
