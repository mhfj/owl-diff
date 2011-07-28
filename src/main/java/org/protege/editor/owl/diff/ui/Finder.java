package org.protege.editor.owl.diff.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.owl.diff.model.DifferenceEvent;
import org.protege.editor.owl.diff.model.DifferenceListener;
import org.protege.editor.owl.diff.model.DifferenceManager;
import org.protege.editor.owl.diff.model.EntityBasedDiffComparator;
import org.protege.editor.owl.diff.ui.render.EntityBasedDiffRenderer;
import org.protege.owl.diff.present.Changes;
import org.protege.owl.diff.present.EntityBasedDiff;
import org.protege.owl.diff.service.RenderingService;

public class Finder extends JPanel {
	private DifferenceManager differenceManager;
	private JButton findButton;
	private JTextField text;
	private DifferenceListener listener = new DifferenceListener() {
		public void statusChanged(DifferenceEvent event) {
			setEnabled(differenceManager.isReady());
		}
	};
	
	public Finder(DifferenceManager differenceManager) {
		setLayout(new FlowLayout());
		this.differenceManager = differenceManager;
		findButton = new JButton("Find");
		findButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				doFind();
			}
		});
		add(findButton);
		text = new JTextField();
		text.setPreferredSize(new JTextField("DNA topoisomerase type 1 activity").getPreferredSize());
		add(text);
		setEnabled(differenceManager.isReady());
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		text.setEnabled(enabled);
		findButton.setEnabled(enabled);
	}
	
	private void doFind() {
		Changes changes = differenceManager.getEngine().getChanges();
		RenderingService renderer = RenderingService.get(differenceManager.getEngine());
		String toMatch = ".*" + text.getText() + ".*";
		List<EntityBasedDiff> diffs = new ArrayList<EntityBasedDiff>();
		for (EntityBasedDiff diff : changes.getEntityBasedDiffs()) {
			if (diff.getSourceEntity() != null && renderer.renderSourceObject(diff.getSourceEntity()).matches(toMatch)) {
				diffs.add(diff);
			}
			else if (diff.getTargetEntity() != null && renderer.renderTargetObject(diff.getTargetEntity()).matches(toMatch)) {
				diffs.add(diff);
			}
		}
		Collections.sort(diffs, new EntityBasedDiffComparator(renderer));
		JDialog dialog = new JDialog();
		dialog.setTitle("Find: " + text.getText());
		dialog.add(new JScrollPane(createSwingList(diffs)));
		dialog.setLocation(new Point(30, 30));
		dialog.setMinimumSize(new Dimension(30, 100));
		dialog.pack();
		dialog.setVisible(true);
	}
	
	private JList createSwingList(List<EntityBasedDiff> diffs) {
		final JList swingList = new JList();
		DefaultListModel model = new DefaultListModel();
		swingList.setModel(model);
		swingList.setSelectionModel(new DefaultListSelectionModel());
		swingList.setCellRenderer(new EntityBasedDiffRenderer(differenceManager));
		swingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		swingList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				Object o = swingList.getSelectedValue();
				if (o instanceof EntityBasedDiff) {
					EntityBasedDiff diff = (EntityBasedDiff) o;
					differenceManager.setSelection(diff);
				}
			}
		});
		for (EntityBasedDiff diff : diffs) {
			model.addElement(diff);
		}
		return swingList;
	}
	
	public void dispose() {
		differenceManager.removeDifferenceListener(listener);
		differenceManager = null;
		text = null;
	}
	
}
