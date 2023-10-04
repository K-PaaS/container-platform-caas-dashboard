package org.caas.dashboard.persistentVolumeClaims;

import lombok.Data;
import org.caas.dashboard.common.model.CommonMetaData;
import org.caas.dashboard.persistentVolumeClaims.support.PersistentVolumeClaimsSpec;
import org.caas.dashboard.persistentVolumeClaims.support.PersistentVolumeClaimsStatus;


import java.util.Map;

/**
 * PersistentVolumeClaims Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-10-24
 */
@Data
public class PersistentVolumeClaims {
    private String resultCode;

    private CommonMetaData metadata;
    private PersistentVolumeClaimsSpec spec;
    private PersistentVolumeClaimsStatus status;

    private Map<String, Object> source;
    private String sourceTypeYaml;
}
