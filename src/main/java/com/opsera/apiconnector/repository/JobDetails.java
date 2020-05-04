package com.opsera.apiconnector.repository;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobDetails {

    private String jobName;

    private Long lastFailedBuildTime;

}
