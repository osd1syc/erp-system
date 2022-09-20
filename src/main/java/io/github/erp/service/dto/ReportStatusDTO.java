package io.github.erp.service.dto;

/*-
 * Erp System - Mark II No 28 (Baruch Series) Server ver 0.1.1-SNAPSHOT
 * Copyright © 2021 - 2022 Edwin Njeru (mailnjeru@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.github.erp.domain.ReportStatus} entity.
 */
public class ReportStatusDTO implements Serializable {

    private Long id;

    @NotNull
    private String reportName;

    @NotNull
    private UUID reportId;

    private Set<PlaceholderDTO> placeholders = new HashSet<>();

    private ProcessStatusDTO processStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public Set<PlaceholderDTO> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(Set<PlaceholderDTO> placeholders) {
        this.placeholders = placeholders;
    }

    public ProcessStatusDTO getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatusDTO processStatus) {
        this.processStatus = processStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportStatusDTO)) {
            return false;
        }

        ReportStatusDTO reportStatusDTO = (ReportStatusDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reportStatusDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportStatusDTO{" +
            "id=" + getId() +
            ", reportName='" + getReportName() + "'" +
            ", reportId='" + getReportId() + "'" +
            ", placeholders=" + getPlaceholders() +
            ", processStatus=" + getProcessStatus() +
            "}";
    }
}
