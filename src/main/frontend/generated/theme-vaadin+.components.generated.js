import { unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin/register-styles';

import vaadinTimePickerCss from 'themes/vaadin+/components/vaadin-time-picker.css?inline';
import vaadinTextFieldCss from 'themes/vaadin+/components/vaadin-text-field.css?inline';
import vaadinTextAreaCss from 'themes/vaadin+/components/vaadin-text-area.css?inline';
import vaadinTabsCss from 'themes/vaadin+/components/vaadin-tabs.css?inline';
import vaadinTabCss from 'themes/vaadin+/components/vaadin-tab.css?inline';
import vaadinSideNavCss from 'themes/vaadin+/components/vaadin-side-nav.css?inline';
import vaadinSideNavItemCss from 'themes/vaadin+/components/vaadin-side-nav-item.css?inline';
import vaadinSelectCss from 'themes/vaadin+/components/vaadin-select.css?inline';
import vaadinRadioGroupCss from 'themes/vaadin+/components/vaadin-radio-group.css?inline';
import vaadinRadioButtonCss from 'themes/vaadin+/components/vaadin-radio-button.css?inline';
import vaadinPasswordFieldCss from 'themes/vaadin+/components/vaadin-password-field.css?inline';
import vaadinNumberFieldCss from 'themes/vaadin+/components/vaadin-number-field.css?inline';
import vaadinNotificationCardCss from 'themes/vaadin+/components/vaadin-notification-card.css?inline';
import vaadinMultiSelectComboBoxCss from 'themes/vaadin+/components/vaadin-multi-select-combo-box.css?inline';
import vaadinMenuBarCss from 'themes/vaadin+/components/vaadin-menu-bar.css?inline';
import vaadinMenuBarButtonCss from 'themes/vaadin+/components/vaadin-menu-bar-button.css?inline';
import vaadinInputContainerCss from 'themes/vaadin+/components/vaadin-input-container.css?inline';
import vaadinEmailFieldCss from 'themes/vaadin+/components/vaadin-email-field.css?inline';
import vaadinDialogOverlayCss from 'themes/vaadin+/components/vaadin-dialog-overlay.css?inline';
import vaadinDateTimePickerCss from 'themes/vaadin+/components/vaadin-date-time-picker.css?inline';
import vaadinDatePickerCss from 'themes/vaadin+/components/vaadin-date-picker.css?inline';
import vaadinComboBoxCss from 'themes/vaadin+/components/vaadin-combo-box.css?inline';
import vaadinCheckboxCss from 'themes/vaadin+/components/vaadin-checkbox.css?inline';
import vaadinCheckboxGroupCss from 'themes/vaadin+/components/vaadin-checkbox-group.css?inline';
import vaadinButtonCss from 'themes/vaadin+/components/vaadin-button.css?inline';
import vaadinAppLayoutCss from 'themes/vaadin+/components/vaadin-app-layout.css?inline';
import uploadCss from 'themes/vaadin+/components/upload.css?inline';
import stepperCss from 'themes/vaadin+/components/stepper.css?inline';
import myComponentListCss from 'themes/vaadin+/components/my-component-list.css?inline';
import keyValuePairsCss from 'themes/vaadin+/components/key-value-pairs.css?inline';
import inputGroupCss from 'themes/vaadin+/components/input-group.css?inline';
import inputFieldCss from 'themes/vaadin+/components/input-field.css?inline';
import dialogCss from 'themes/vaadin+/components/dialog.css?inline';
import breadcrumbCss from 'themes/vaadin+/components/breadcrumb.css?inline';


if (!document['_vaadintheme_vaadin+_componentCss']) {
  registerStyles(
        'vaadin-time-picker',
        unsafeCSS(vaadinTimePickerCss.toString())
      );
      registerStyles(
        'vaadin-text-field',
        unsafeCSS(vaadinTextFieldCss.toString())
      );
      registerStyles(
        'vaadin-text-area',
        unsafeCSS(vaadinTextAreaCss.toString())
      );
      registerStyles(
        'vaadin-tabs',
        unsafeCSS(vaadinTabsCss.toString())
      );
      registerStyles(
        'vaadin-tab',
        unsafeCSS(vaadinTabCss.toString())
      );
      registerStyles(
        'vaadin-side-nav',
        unsafeCSS(vaadinSideNavCss.toString())
      );
      registerStyles(
        'vaadin-side-nav-item',
        unsafeCSS(vaadinSideNavItemCss.toString())
      );
      registerStyles(
        'vaadin-select',
        unsafeCSS(vaadinSelectCss.toString())
      );
      registerStyles(
        'vaadin-radio-group',
        unsafeCSS(vaadinRadioGroupCss.toString())
      );
      registerStyles(
        'vaadin-radio-button',
        unsafeCSS(vaadinRadioButtonCss.toString())
      );
      registerStyles(
        'vaadin-password-field',
        unsafeCSS(vaadinPasswordFieldCss.toString())
      );
      registerStyles(
        'vaadin-number-field',
        unsafeCSS(vaadinNumberFieldCss.toString())
      );
      registerStyles(
        'vaadin-notification-card',
        unsafeCSS(vaadinNotificationCardCss.toString())
      );
      registerStyles(
        'vaadin-multi-select-combo-box',
        unsafeCSS(vaadinMultiSelectComboBoxCss.toString())
      );
      registerStyles(
        'vaadin-menu-bar',
        unsafeCSS(vaadinMenuBarCss.toString())
      );
      registerStyles(
        'vaadin-menu-bar-button',
        unsafeCSS(vaadinMenuBarButtonCss.toString())
      );
      registerStyles(
        'vaadin-input-container',
        unsafeCSS(vaadinInputContainerCss.toString())
      );
      registerStyles(
        'vaadin-email-field',
        unsafeCSS(vaadinEmailFieldCss.toString())
      );
      registerStyles(
        'vaadin-dialog-overlay',
        unsafeCSS(vaadinDialogOverlayCss.toString())
      );
      registerStyles(
        'vaadin-date-time-picker',
        unsafeCSS(vaadinDateTimePickerCss.toString())
      );
      registerStyles(
        'vaadin-date-picker',
        unsafeCSS(vaadinDatePickerCss.toString())
      );
      registerStyles(
        'vaadin-combo-box',
        unsafeCSS(vaadinComboBoxCss.toString())
      );
      registerStyles(
        'vaadin-checkbox',
        unsafeCSS(vaadinCheckboxCss.toString())
      );
      registerStyles(
        'vaadin-checkbox-group',
        unsafeCSS(vaadinCheckboxGroupCss.toString())
      );
      registerStyles(
        'vaadin-button',
        unsafeCSS(vaadinButtonCss.toString())
      );
      registerStyles(
        'vaadin-app-layout',
        unsafeCSS(vaadinAppLayoutCss.toString())
      );
      registerStyles(
        'upload',
        unsafeCSS(uploadCss.toString())
      );
      registerStyles(
        'stepper',
        unsafeCSS(stepperCss.toString())
      );
      registerStyles(
        'my-component-list',
        unsafeCSS(myComponentListCss.toString())
      );
      registerStyles(
        'key-value-pairs',
        unsafeCSS(keyValuePairsCss.toString())
      );
      registerStyles(
        'input-group',
        unsafeCSS(inputGroupCss.toString())
      );
      registerStyles(
        'input-field',
        unsafeCSS(inputFieldCss.toString())
      );
      registerStyles(
        'dialog',
        unsafeCSS(dialogCss.toString())
      );
      registerStyles(
        'breadcrumb',
        unsafeCSS(breadcrumbCss.toString())
      );
      
  document['_vaadintheme_vaadin+_componentCss'] = true;
}

if (import.meta.hot) {
  import.meta.hot.accept((module) => {
    window.location.reload();
  });
}

