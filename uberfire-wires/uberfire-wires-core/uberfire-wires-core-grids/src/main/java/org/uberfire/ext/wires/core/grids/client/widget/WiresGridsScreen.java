/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.IGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.IGridData;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridData;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridCell;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridData;
import org.uberfire.ext.wires.core.grids.client.util.GridDataFactory;
import org.uberfire.ext.wires.core.grids.client.widget.basic.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.CheckBoxDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.dom.TextBoxDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.TextBoxDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.dom.TextBoxSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.edit.EditorPopup;
import org.uberfire.ext.wires.core.grids.client.widget.mergable.MergableGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.basic.BlueGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.basic.GreenGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.basic.RedGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.mergable.MergableGridRenderer;

@Dependent
@WorkbenchScreen(identifier = "WiresGridsScreen")
public class WiresGridsScreen extends Composite implements ISelectionManager {

    public static final int VP_WIDTH = 1200;
    public static final int VP_HEIGHT = 600;

    private static final double VP_SCALE = 1.0;

    private static final int GRID1_ROWS = 100;
    private static final int GRID2_ROWS = 100;
    private static final int GRID3_ROWS = 100;
    private static final int GRID4_ROWS = 100;

    interface WiresGridsScreenUiBinder extends UiBinder<Widget, WiresGridsScreen> {

    }

    private static WiresGridsScreenUiBinder uiBinder = GWT.create( WiresGridsScreenUiBinder.class );

    @UiField
    AbsolutePanel domElementContainer;

    @UiField
    ListBox zoom;

    @UiField
    ListBox basicRendererSelector;

    @UiField
    CheckBox chkShowMerged;

    private final EditorPopup editor = new EditorPopup();

    private GridLayer gridLayer = new GridLayer();
    private LienzoPanel gridPanel = new LienzoPanel( VP_WIDTH,
                                                     VP_HEIGHT );

    public WiresGridsScreen() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @WorkbenchPartTitle
    @Override
    public String getTitle() {
        return "Grids";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

    @PostConstruct
    public void setup() {
        //Lienzo stuff - Set default scale
        final Transform transform = new Transform().scale( VP_SCALE );
        gridPanel.getViewport().setTransform( transform );

        //Lienzo stuff - Add mouse pan support
        final MousePanMediator mediator1 = new MousePanMediator();
        gridPanel.getViewport().getMediators().push( mediator1 );

        //Wire-up widgets
        gridPanel.add( gridLayer );
        domElementContainer.add( gridPanel );

        gridLayer.addNodeMouseClickHandler( new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick( NodeMouseClickEvent nodeMouseClickEvent ) {
                gridPanel.setFocus( true );
            }
        } );

        //Grid 1
        final MergableGridData grid1 = new MergableGridData();
        final MergableGridWidget gridWidget1 = new MergableGridWidget( grid1,
                                                                       this,
                                                                       new MergableGridRenderer() );
        for ( int idx = 0; idx < 10; idx++ ) {
            final int grid1ColumnGroupSuffix = ( idx < 5 ? 0 : 1 );
            final Double minimumColumnWidth = ( idx == 1 ? 75.0 : 100.0 );
            final Double maximumColumnWidth = ( idx == 1 ? 200.0 : null );
            final MergableGridColumn<String> grid1Column = new MergableGridColumn<String>( "G1-G" + grid1ColumnGroupSuffix + "-C" + idx,
                                                                                           minimumColumnWidth ) {
                @Override
                public void renderCell( final Group g,
                                        final MergableGridCell<String> cell,
                                        final GridCellRenderContext context ) {
                    final Text t = new Text( cell.getValue().getValue() )
                            .setFillColor( ColorName.GREY )
                            .setFontSize( 12 )
                            .setFontFamily( "serif" )
                            .setListening( false )
                            .setTextBaseLine( TextBaseLine.MIDDLE )
                            .setTextAlign( TextAlign.CENTER )
                            .setX( context.getWidth() / 2 )
                            .setY( context.getHeight() / 2 );
                    g.add( t );
                }

                @Override
                public void edit( final MergableGridCell<String> cell,
                                  final GridCellRenderContext context,
                                  final Callback<IGridCellValue<String>> callback ) {
                    editor.edit( cell == null ? null : cell.getValue(),
                                 callback );
                }

                @Override
                public String getColumnGroup() {
                    return "grid1ColumnGroup" + grid1ColumnGroupSuffix;
                }

                @Override
                public Double getMinimumColumnWidth() {
                    return minimumColumnWidth;
                }

                @Override
                public Double getMaximumColumnWidth() {
                    return maximumColumnWidth;
                }
            };
            grid1.appendColumn( grid1Column );
        }
        GridDataFactory.populate( grid1,
                                  GRID1_ROWS );

        //Grid 2
        final MergableGridData grid2 = new MergableGridData();
        final MergableGridWidget gridWidget2 = new MergableGridWidget( grid2,
                                                                       this,
                                                                       new MergableGridRenderer() );
        for ( int idx = 0; idx < 5; idx++ ) {
            final MergableGridColumn<String> grid2Column = new MergableGridColumn<String>( "G2-G0-C" + idx,
                                                                                           150 ) {
                @Override
                public void renderCell( final Group g,
                                        final MergableGridCell<String> cell,
                                        final GridCellRenderContext context ) {
                    final Text t = new Text( cell.getValue().getValue() )
                            .setFillColor( ColorName.GREY )
                            .setFontSize( 12 )
                            .setFontFamily( "serif" )
                            .setListening( false )
                            .setTextBaseLine( TextBaseLine.MIDDLE )
                            .setTextAlign( TextAlign.CENTER )
                            .setX( context.getWidth() / 2 )
                            .setY( context.getHeight() / 2 );
                    g.add( t );
                }

                @Override
                public void edit( final MergableGridCell<String> cell,
                                  final GridCellRenderContext context,
                                  final Callback<IGridCellValue<String>> callback ) {
                    editor.edit( cell == null ? null : cell.getValue(),
                                 callback );
                }

            };
            grid2.appendColumn( grid2Column );
        }
        GridDataFactory.populate( grid2,
                                  GRID2_ROWS );

        //Grid 3
        final MergableGridData grid3 = new MergableGridData();
        final MergableGridWidget gridWidget3 = new MergableGridWidget( grid3,
                                                                       this,
                                                                       new MergableGridRenderer() );
        for ( int idx = 0; idx < 2; idx++ ) {
            final boolean isResizeable = idx != 1;
            final boolean isMoveable = idx != 1;
            final MergableGridColumn<String> grid3Column = new MergableGridColumn<String>( "G3-G0-C" + idx,
                                                                                           100 ) {
                @Override
                public void renderCell( final Group g,
                                        final MergableGridCell<String> cell,
                                        final GridCellRenderContext context ) {
                    final Text t = new Text( cell.getValue().getValue() )
                            .setFillColor( ColorName.GREY )
                            .setFontSize( 12 )
                            .setFontFamily( "serif" )
                            .setListening( false )
                            .setTextBaseLine( TextBaseLine.MIDDLE )
                            .setTextAlign( TextAlign.CENTER )
                            .setX( context.getWidth() / 2 )
                            .setY( context.getHeight() / 2 );
                    g.add( t );
                }

                @Override
                public void edit( final MergableGridCell<String> cell,
                                  final GridCellRenderContext context,
                                  final Callback<IGridCellValue<String>> callback ) {
                    editor.edit( cell == null ? null : cell.getValue(),
                                 callback );
                }

                @Override
                public boolean isResizable() {
                    return isResizeable;
                }

                @Override
                public boolean isMoveable() {
                    return isMoveable;
                }

            };
            grid3.appendColumn( grid3Column );
        }
        GridDataFactory.populate( grid3,
                                  GRID3_ROWS );

        //Grid 3 - DOM Column - TextBox (Lazy show)
        final String grid3ColumnGroup1 = "grid3ColumnGroup1";
        final MergableGridColumn<String> grid3Column2 = new MergableGridColumn<String>( "G3-G1-C2",
                                                                                        100 ) {

            private TextBoxSingletonDOMElementFactory factory = new TextBoxSingletonDOMElementFactory( gridLayer,
                                                                                                       gridWidget3,
                                                                                                       domElementContainer );

            @Override
            public void renderCell( final Group g,
                                    final MergableGridCell<String> cell,
                                    final GridCellRenderContext context ) {
                final Text t = new Text( cell.getValue().getValue() )
                        .setFillColor( ColorName.GREY )
                        .setFontSize( 12 )
                        .setFontFamily( "serif" )
                        .setListening( false )
                        .setTextBaseLine( TextBaseLine.MIDDLE )
                        .setTextAlign( TextAlign.CENTER )
                        .setX( context.getWidth() / 2 )
                        .setY( context.getHeight() / 2 );
                g.add( t );
            }

            @Override
            public void edit( final MergableGridCell<String> cell,
                              final GridCellRenderContext context,
                              final Callback<IGridCellValue<String>> callback ) {
                factory.attachDomElement( cell,
                                          context,
                                          new Callback<TextBoxDOMElement>() {
                                              @Override
                                              public void callback( final TextBoxDOMElement e ) {
                                                  e.getWidget().setFocus( true );
                                              }
                                          } );
            }

            @Override
            public void initialiseResources() {
                factory.initialiseResources();
            }

            @Override
            public void destroyResources() {
                factory.destroyResources();
            }

            @Override
            public void freeUnusedResources() {
                factory.freeUnusedResources();
            }

            @Override
            public String getColumnGroup() {
                return grid3ColumnGroup1;
            }

        };
        grid3.appendColumn( grid3Column2 );
        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            grid3.setCell( rowIndex,
                           2,
                           new BaseGridCellValue<String>( "(" + 2 + ", " + rowIndex + ")" ) );
        }

        //Grid 3 - DOM Column - CheckBox
        final MergableGridColumn<Boolean> grid3Column3 = new MergableGridColumn<Boolean>( "G3-G1-C3",
                                                                                          100 ) {

            private CheckBoxDOMElementFactory factory = new CheckBoxDOMElementFactory( gridLayer,
                                                                                       gridWidget3,
                                                                                       domElementContainer );

            @Override
            public void renderCell( final Group g,
                                    final MergableGridCell<Boolean> cell,
                                    final GridCellRenderContext context ) {
                factory.attachDomElement( cell,
                                          context );
            }

            @Override
            public void initialiseResources() {
                factory.initialiseResources();
            }

            @Override
            public void destroyResources() {
                factory.destroyResources();
            }

            @Override
            public void freeUnusedResources() {
                factory.freeUnusedResources();
            }

            @Override
            public String getColumnGroup() {
                return grid3ColumnGroup1;
            }

        };
        grid3.appendColumn( grid3Column3 );
        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            grid3.setCell( rowIndex,
                           3,
                           new BaseGridCellValue<Boolean>( Math.random() < GridDataFactory.FILL_FACTOR ) );
        }

        //Grid 3 - DOM Column - TextBox
        final MergableGridColumn<String> grid3Column4 = new MergableGridColumn<String>( "G3-G1-C4",
                                                                                        100 ) {

            private TextBoxDOMElementFactory factory = new TextBoxDOMElementFactory( gridLayer,
                                                                                     gridWidget3,
                                                                                     domElementContainer );

            @Override
            public void renderCell( final Group g,
                                    final MergableGridCell<String> cell,
                                    final GridCellRenderContext context ) {
                factory.attachDomElement( cell,
                                          context );
            }

            @Override
            public void initialiseResources() {
                factory.initialiseResources();
            }

            @Override
            public void destroyResources() {
                factory.destroyResources();
            }

            @Override
            public void freeUnusedResources() {
                factory.freeUnusedResources();
            }

            @Override
            public String getColumnGroup() {
                return grid3ColumnGroup1;
            }

        };
        grid3.appendColumn( grid3Column4 );
        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            if ( Math.random() < GridDataFactory.FILL_FACTOR ) {
                grid3.setCell( rowIndex,
                               4,
                               new BaseGridCellValue<String>( "(" + 4 + ", " + rowIndex + ")" ) );
            }
        }

        //Grid 4
        final GridData grid4 = new GridData();
        final GridWidget gridWidget4 = new GridWidget( grid4,
                                                       this,
                                                       new RedGridRenderer() );

        //Grid 4 - DOM Column - TextBox
        final GridColumn<String> grid4Column1 = new GridColumn<String>( "G4-G0-C0",
                                                                        100 ) {

            @Override
            public void renderCell( final Group g,
                                    final GridCell<String> cell,
                                    final GridCellRenderContext context ) {
                final Text t = new Text( cell.getValue().getValue() )
                        .setFillColor( ColorName.GREY )
                        .setFontSize( 12 )
                        .setFontFamily( "serif" )
                        .setListening( false )
                        .setTextBaseLine( TextBaseLine.MIDDLE )
                        .setTextAlign( TextAlign.CENTER )
                        .setX( context.getWidth() / 2 )
                        .setY( context.getHeight() / 2 );
                g.add( t );
            }

            @Override
            public void edit( final GridCell<String> cell,
                              final GridCellRenderContext context,
                              final Callback<IGridCellValue<String>> callback ) {
                editor.edit( cell == null ? null : cell.getValue(),
                             callback );
            }

        };
        grid4.appendColumn( grid4Column1 );

        //Grid 4 - DOM Column - CheckBox
        final GridColumn<Boolean> grid4Column2 = new GridColumn<Boolean>( "G4-G0-C1",
                                                                          100 ) {

            private CheckBoxDOMElementFactory factory = new CheckBoxDOMElementFactory( gridLayer,
                                                                                       gridWidget4,
                                                                                       domElementContainer );

            @Override
            public void renderCell( final Group g,
                                    final GridCell<Boolean> cell,
                                    final GridCellRenderContext context ) {
                factory.attachDomElement( cell,
                                          context );
            }

            @Override
            public void initialiseResources() {
                factory.initialiseResources();
            }

            @Override
            public void destroyResources() {
                factory.destroyResources();
            }

            @Override
            public void freeUnusedResources() {
                factory.freeUnusedResources();
            }

        };
        grid4.appendColumn( grid4Column2 );

        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            final GridRow row = new GridRow();
            grid4.appendRow( row );
            for ( int columnIndex = 0; columnIndex < grid4.getColumns().size(); columnIndex++ ) {
                switch ( columnIndex ) {
                    case 0:
                        grid4.setCell( rowIndex,
                                       columnIndex,
                                       new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
                        break;
                    case 1:
                        grid4.setCell( rowIndex,
                                       columnIndex,
                                       new BaseGridCellValue<Boolean>( Math.random() < 0.5 ) );
                        break;
                }
            }
        }

        //Link grids
        grid1.getColumns().get( 9 ).setLink( grid2.getColumns().get( 0 ) );
        grid2.getColumns().get( 3 ).setLink( grid3.getColumns().get( 0 ) );
        grid3.getColumns().get( 0 ).setLink( grid1.getColumns().get( 0 ) );

        //Add Widgets to the Layer
        gridWidget1.setLocation( new Point2D( -1300,
                                              0 ) );
        gridWidget2.setLocation( new Point2D( 0,
                                              750 ) );
        gridWidget3.setLocation( new Point2D( 1050,
                                              0 ) );
        gridWidget4.setLocation( new Point2D( 1800,
                                              0 ) );
        gridLayer.add( gridWidget1 );
        gridLayer.add( gridWidget2 );
        gridLayer.add( gridWidget3 );
        gridLayer.add( gridWidget4 );

        //Slider
        for ( int pct = 50; pct <= 150; pct = pct + 10 ) {
            zoom.addItem( Integer.toString( pct ) );
        }
        zoom.addChangeHandler( new ChangeHandler() {

            private double m_currentZoom = 1.0;

            @Override
            public void onChange( final ChangeEvent event ) {
                final int selectedIndex = zoom.getSelectedIndex();
                if ( selectedIndex < 0 ) {
                    return;
                }
                final double pct = Double.parseDouble( zoom.getValue( selectedIndex ) );
                final int compare = Double.compare( m_currentZoom,
                                                    pct );
                if ( compare == 0 ) {
                    return;
                }
                m_currentZoom = pct;

                final Transform transform = new Transform();
                final double tx = gridPanel.getViewport().getTransform().getTranslateX();
                final double ty = gridPanel.getViewport().getTransform().getTranslateY();
                transform.translate( tx, ty );
                transform.scale( m_currentZoom / 100 );

                gridPanel.getViewport().setTransform( transform );
                gridPanel.getViewport().draw();
            }

        } );

        //Style selectors
        final Map<String, IGridRenderer<GridData>> basicRenderers = new HashMap<String, IGridRenderer<GridData>>();
        final RedGridRenderer redRenderer = new RedGridRenderer();
        final GreenGridRenderer greenRenderer = new GreenGridRenderer();
        final BlueGridRenderer blueRenderer = new BlueGridRenderer();
        basicRenderers.put( redRenderer.getName(),
                            redRenderer );
        basicRenderers.put( greenRenderer.getName(),
                            greenRenderer );
        basicRenderers.put( blueRenderer.getName(),
                            blueRenderer );
        for ( String name : basicRenderers.keySet() ) {
            basicRendererSelector.addItem( name );
        }
        basicRendererSelector.addChangeHandler( new ChangeHandler() {
            @Override
            @SuppressWarnings("unused")
            public void onChange( final ChangeEvent event ) {
                final IGridRenderer<GridData> renderer = basicRenderers.get( basicRendererSelector.getItemText( basicRendererSelector.getSelectedIndex() ) );
                gridWidget4.setRenderer( renderer );
                gridLayer.draw();
            }
        } );

        //Merged indicator
        chkShowMerged.setValue( grid1.isMerged() );
        chkShowMerged.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            @SuppressWarnings("unused")
            public void onValueChange( final ValueChangeEvent<Boolean> event ) {
                grid1.setMerged( chkShowMerged.getValue() );
                grid2.setMerged( chkShowMerged.getValue() );
                grid3.setMerged( chkShowMerged.getValue() );
                gridLayer.draw();
            }
        } );

        //Prevent DOMElements scrolling into view when they receive the focus
        domElementContainer.addDomHandler( new ScrollHandler() {

            @Override
            public void onScroll( ScrollEvent scrollEvent ) {
                domElementContainer.getElement().setScrollTop( 0 );
                domElementContainer.getElement().setScrollLeft( 0 );
            }
        }, ScrollEvent.getType() );
    }

    @Override
    public void select( final IGridData<?, ?, ?> selectable ) {
        gridLayer.select( selectable );
    }

    @Override
    public void selectLinkedColumn( final IGridColumn<?, ?> link ) {
        gridLayer.selectLinkedColumn( link );
    }

}
