/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.Window;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.resources.CommonImages;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class RenamePopup extends FormStylePopup {

    final private TextBox nameTextBox = new TextBox();
    final private TextBox checkInCommentTextBox = new TextBox();

    public RenamePopup( final Path path,
                        final CommandWithFileNameAndCommitMessage command ) {
        this( path,
              new Validator() {
                  @Override
                  public void validate( final String value,
                                        final ValidatorCallback callback ) {
                      callback.onSuccess();
                  }
              },
              command );
    }

    public RenamePopup( final Path path,
                        final Validator validator,
                        final CommandWithFileNameAndCommitMessage command ) {
        super( CommonImages.INSTANCE.edit(),
               CommonConstants.INSTANCE.RenamePopupTitle() );

        checkNotNull( "validator",
                      validator );
        checkNotNull( "path",
                      path );
        checkNotNull( "command",
                      command );

        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        nameTextBox.setTitle( CommonConstants.INSTANCE.NewName() );
        nameTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.NewNameColon(),
                      nameTextBox );

        checkInCommentTextBox.setTitle( CommonConstants.INSTANCE.CheckInComment() );
        checkInCommentTextBox.setWidth( "200px" );
        addAttribute( CommonConstants.INSTANCE.CheckInCommentColon(),
                      checkInCommentTextBox );

        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( CommonConstants.INSTANCE.RenamePopupRenameItem(),
                          new Command() {
                              @Override
                              public void execute() {
                                  final String baseFileName = nameTextBox.getText();
                                  final String originalFileName = path.getFileName();
                                  final String extension = ( originalFileName.lastIndexOf( "." ) > 0 ? originalFileName.substring( originalFileName.lastIndexOf( "." ) ) : "" );
                                  final String fileName = baseFileName + extension;

                                  validator.validate( fileName,
                                                      new ValidatorCallback() {
                                                          @Override
                                                          public void onSuccess() {
                                                              hide();
                                                              command.execute( new FileNameAndCommitMessage( baseFileName,
                                                                                                             checkInCommentTextBox.getText() ) );
                                                          }

                                                          @Override
                                                          public void onFailure() {
                                                              Window.alert( CommonConstants.INSTANCE.InvalidFileName0( baseFileName ) );
                                                          }
                                                      } );
                              }
                          },
                          IconType.SAVE,
                          ButtonType.PRIMARY );
        footer.addButton( CommonConstants.INSTANCE.Cancel(),
                          new Command() {
                              @Override
                              public void execute() {
                                  hide();
                              }
                          },
                          ButtonType.DEFAULT );
        add( footer );
    }

}
