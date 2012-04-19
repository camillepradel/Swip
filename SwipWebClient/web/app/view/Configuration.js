Ext.define('SWIP.view.Configuration', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.configuration',
    items: [
    {
        xtype: 'fieldcontainer',
        style: 'margin-left:5px;font-weight:bold;color:#385F95;font-size: 13px;',
        allowBlank: false,
        fieldLabel: 'Search Output',
        labelWidth: '100',
        defaultType: 'checkboxfield',
        columns: 1,
        vertical: true,
        items: [ 
        {
            boxLabel  : 'Mapping',
            name      : 'result',
            checked   : true,
            inputValue: '1',
            itemId : 'checkbox1',
            id        : 'chbx1'
        }, {
            boxLabel  : 'Matching',
            name      : 'result',
            inputValue: '2',
            itemId : 'checkbox2',
            id        : 'chbx2'
        } 
        ]
    }
    ],
    bbar: [
    {
        text: 'Select All',
        handler: function() {
      
            Ext.getCmp('chbx1').setValue(true);
            Ext.getCmp('chbx2').setValue(true);
        }
    },
    {
        text: 'Deselect All',
        handler: function() {
            Ext.getCmp('chbx1').setValue(false);
            Ext.getCmp('chbx2').setValue(false);
        }
    }
    ],
    initComponent: function() {
        this.callParent();
    }
});



