package io.github.erp.internal.framework;

/*-
 * Erp System - Mark II No 26 (Baruch Series) Server ver 0.0.6-SNAPSHOT
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

/**
 * Marker interface for models containing data that has been deserialized from an excel file
 */
public interface ExcelViewModel<M> extends Serializable {
    Long getRowIndex();

    String getFileUploadToken();

    M getModelData();
}
