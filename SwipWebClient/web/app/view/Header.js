Ext.define('SWIP.view.Header', {
    extend: 'Ext.Component',
    height: 40,
    alias    : 'widget.swipheader',
    componentCls: 'app-header',
    
    initComponent: function() {
        Ext.applyIf(this, {
          html: 'SWIP : Semantic Web Interface using Patterns'
     });
     this.callParent(arguments);
    }
});


                   