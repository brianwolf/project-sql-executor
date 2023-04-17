package com.toyota.sqlexecutor.models.executables;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class SQLExecutable {

	private List<String> resultColumns;

	public SQLExecutable() {
		this.resultColumns = new ArrayList<>();
	}
}
