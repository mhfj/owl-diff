package org.protege.editor.owl.diff.ui;

import org.protege.editor.owl.diff.model.DifferenceManager;
import org.protege.owl.diff.present.MatchedAxiom;
import org.protege.owl.diff.service.RenderingService;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class DifferenceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -1919687859946498484L;

	public enum Column {
		DESCRIPTION("Description"), 
		SOURCE_AXIOM("Baseline Axiom"), 
		TARGET_AXIOM("New Axiom");
		
		private String name;
		
		private Column(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private DifferenceManager diffs;
	private List<MatchedAxiom> matches = new ArrayList<MatchedAxiom>();
	
	public DifferenceTableModel(DifferenceManager diffs) {
		this.diffs = diffs;
	}
	
	public void setMatches(SortedSet<MatchedAxiom> matches) {
		this.matches = new ArrayList<MatchedAxiom>(matches);
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return Column.values().length;
	}

	public int getRowCount() {
		return matches == null ? 0 : matches.size();
	}
	
	public void clear() {
		matches.clear();
	}
	
	public String getColumnName(int column) {
		return Column.values()[column].toString();
	}
	
	public Class<?> getColumnClass(int col) {
		switch (Column.values()[col]) {
		case DESCRIPTION:
			return String.class;
		case SOURCE_AXIOM:
		case TARGET_AXIOM:
			return String.class;
		default:
			throw new IllegalStateException("Programmer error");
		}
	}


	public Object getValueAt(int row, int col) {
		RenderingService renderer = RenderingService.get(diffs.getEngine());
		OWLAxiom axiom;
		MatchedAxiom match = matches.get(row);
		switch (Column.values()[col]) {
		case DESCRIPTION:
			return match.getDescription();
		case SOURCE_AXIOM:
			axiom = match.getSourceAxiom();
			return axiom == null ? "" : renderer.renderSourceObject(axiom);
			// return axiom;
		case TARGET_AXIOM:
			axiom = match.getTargetAxiom();
			return axiom == null ? "" : renderer.renderTargetObject(axiom);
			// return axiom;
		default:
			throw new IllegalStateException("Programmer error");
		}
	}

}
