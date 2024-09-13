(function() {

  const changeListener = function(e) {
    this.inputElement.value = parseFloat(this.value.split(this._decimalSeparator).join('.')).toFixed(2).split('.').join(this._decimalSeparator);
  };

  customElements.whenDefined('vaadin-big-decimal-field').then(() => {

    class MyBigDecimalField extends customElements.get('vaadin-big-decimal-field') {

      static get is() {
        return 'my-big-decimal-field';
      }

      ready() {
        super.ready();
        this.addEventListener('change', changeListener);
      }

    }

    customElements.define(MyBigDecimalField.is, MyBigDecimalField);

  });
})();