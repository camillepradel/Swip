Ext.define('SWIP.view.search.SearchPagingToolbar', {
    extend   : 'Ext.PagingToolbar',
    alias    : 'widget.searchpagingtoolbar',
    requires : ['Ext.button.*'],
    pageSize: 25,
    displayInfo:true,
    emptyMsg: 'No results to display',
    displayMsg: 'Displaying items {0} - {1} of {2}',
    beforePageText: 'Page',
    plugins: Ext.create('Ext.ux.ProgressBarPager', {}),
    store :'Mappings',
    items:[{
        xtype: 'button',
        text : 'Validate results',
        iconCls: 'validate',
        action: 'validate'
    }],

    initComponent : function() {
        this.callParent();
    }
});


