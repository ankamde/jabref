package org.jabref.gui.sidepane;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.undo.UndoManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jabref.gui.ClipBoardManager;
import org.jabref.gui.DialogService;
import org.jabref.gui.LibraryTabContainer;
import org.jabref.gui.StateManager;
import org.jabref.gui.entryeditor.AdaptVisibleTabs;
import org.jabref.gui.frame.SidePanePreferences;
import org.jabref.gui.preferences.GuiPreferences;
import org.jabref.gui.util.CustomLocalDragboard;
import org.jabref.logic.ai.AiService;
import org.jabref.logic.journals.JournalAbbreviationRepository;
import org.jabref.logic.util.OptionalObjectProperty;
import org.jabref.logic.util.TaskExecutor;
import org.jabref.model.entry.BibEntryTypesManager;
import org.jabref.model.util.FileUpdateMonitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class SidePaneViewModelTest {

    LibraryTabContainer tabContainer = mock(LibraryTabContainer.class);
    GuiPreferences preferences = mock(GuiPreferences.class);
    JournalAbbreviationRepository abbreviationRepository = mock(JournalAbbreviationRepository.class);
    StateManager stateManager = mock(StateManager.class);
    TaskExecutor taskExecutor = mock(TaskExecutor.class);
    AdaptVisibleTabs adaptVisibleTabs = mock(AdaptVisibleTabs.class);
    DialogService dialogService = mock(DialogService.class);
    AiService aiService = mock(AiService.class);
    FileUpdateMonitor fileUpdateMonitor = mock(FileUpdateMonitor.class);
    BibEntryTypesManager entryTypesManager = mock(BibEntryTypesManager.class);
    ClipBoardManager clipBoardManager = mock(ClipBoardManager.class);
    UndoManager undoManager = mock(UndoManager.class);

    SidePanePreferences sidePanePreferences = new SidePanePreferences(new HashSet<>(), new HashMap<>(), 0);
    ObservableList<SidePaneType> sidePaneComponents = FXCollections.observableArrayList();
    SidePaneViewModel sidePaneViewModel;

    @BeforeEach
    void setUp() {
        when(stateManager.getVisibleSidePaneComponents()).thenReturn(sidePaneComponents);
        when(stateManager.getLocalDragboard()).thenReturn(mock(CustomLocalDragboard.class));
        when(stateManager.activeDatabaseProperty()).thenReturn(OptionalObjectProperty.empty());
        when(preferences.getSidePanePreferences()).thenReturn(sidePanePreferences);

        sidePanePreferences.visiblePanes().addAll(EnumSet.allOf(SidePaneType.class));
        sidePanePreferences.getPreferredPositions().put(SidePaneType.GROUPS, 0);
        sidePanePreferences.getPreferredPositions().put(SidePaneType.WEB_SEARCH, 1);
        sidePanePreferences.getPreferredPositions().put(SidePaneType.OPEN_OFFICE, 2);

        sidePaneViewModel = new SidePaneViewModel(
                tabContainer,
                preferences,
                abbreviationRepository,
                stateManager,
                taskExecutor,
                adaptVisibleTabs,
                dialogService,
                aiService,
                fileUpdateMonitor,
                entryTypesManager,
                clipBoardManager,
                undoManager);
    }

    @Test
    void moveUp() {
        sidePaneViewModel.moveUp(SidePaneType.WEB_SEARCH);

        assertEquals(SidePaneType.WEB_SEARCH, sidePaneComponents.getFirst());
        assertEquals(SidePaneType.GROUPS, sidePaneComponents.get(1));
    }

    @Test
    void moveUpFromFirstPosition() {
        sidePaneViewModel.moveUp(SidePaneType.GROUPS);

        assertEquals(SidePaneType.GROUPS, sidePaneComponents.getFirst());
    }

    @Test
    void moveDown() {
        sidePaneViewModel.moveDown(SidePaneType.WEB_SEARCH);

        assertEquals(SidePaneType.OPEN_OFFICE, sidePaneComponents.get(1));
        assertEquals(SidePaneType.WEB_SEARCH, sidePaneComponents.get(2));
    }

    @Test
    void moveDownFromLastPosition() {
        sidePaneViewModel.moveDown(SidePaneType.OPEN_OFFICE);

        assertEquals(SidePaneType.OPEN_OFFICE, sidePaneComponents.get(2));
    }

    @Test
    void sortByPreferredPositions() {
        sidePanePreferences.getPreferredPositions().put(SidePaneType.GROUPS, 2);
        sidePanePreferences.getPreferredPositions().put(SidePaneType.OPEN_OFFICE, 0);

        sidePaneComponents.sort(new SidePaneViewModel.PreferredIndexSort(sidePanePreferences));

        assertTrue(sidePaneComponents.getFirst() == SidePaneType.OPEN_OFFICE && sidePaneComponents.get(2) == SidePaneType.GROUPS);
    }
}
