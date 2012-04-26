Ext.define('SWIP.view.Options', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.swipoptions',
    autoScroll: true,
    collapsible: true,
    animCollapse: true,
    title: 'Options',
    layout: 'accordion',
    layoutConfig:{
        animate: true
    },
    collapseDirection: Ext.Component.DIRECTION_LEFT,
	
    items: [{
        title: 'Settings',
        xtype: 'configuration',
        iconCls: 'settings'
    },
    {
        title: 'Credits',
        html: '<div class="search-item"><dl><dt><h4>System</h4></dt>\n\
                                    <dd>- Camille Pradel</dd>\n\
                                    <dd>- Ollivier Haemmerlé</dd>\n\
                                    <dd>- Nathalie Hernandez</dd>\n\
                                  <dt><h4>Interface</h4></dt>\n\
                                    <dd>- Anna Lanevska</dd></dl></div>',
        iconCls: 'copyright'
    }],
    initComponent: function() {
        this.callParent();
    }
});
