package org.toyota.sqlexecutor.models.connections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SQLJndiConnection {

	private String urlConnection;

}
