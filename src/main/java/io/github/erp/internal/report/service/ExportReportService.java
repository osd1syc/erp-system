package io.github.erp.internal.report.service;

/*-
 * Erp System - Mark X No 7 (Jehoiada Series) Server ver 1.7.9
 * Copyright © 2021 - 2024 Edwin Njeru and the ERP System Contributors (mailnjeru@gmail.com)
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

/**
 * This service takes a report requisition out of which configurations for
 * a report, and the parameters are applied to create a report which is exported
 * in the desired format to the prescribed or expected destination
 *
 * @param <DTO> This is the type of the requisition object containing the report
 *             instructions, parameters and configurations
 */
public interface ExportReportService<DTO> {

    void exportReport(DTO reportRequisition);
}