Ext.define('SWIP.view.search.SearchBar', {
    extend   : 'Ext.form.field.ComboBox',
    alias    : 'widget.searchbar',
    store    : 'HistoryRecords', 
    fieldLabel: 'Search',
    labelWidth: 50,
    trigger1Cls: Ext.baseCSSPrefix + 'form-clear-trigger',
    trigger2Cls: Ext.baseCSSPrefix + 'form-search-trigger',
    paramNames:{
        plquery:'plquery', 
        mquery:'mquery'
    },
    displayField: 'query',
    autoSelect: true,
    typeAhead: true, 
    queryMode: 'local',
    minChars:           1,
    listConfig: {
        getInnerTpl: function() {
            return '<div class="search-item-combo" ><h3>{query}<span>{[Ext.Date.format(values.searchDate, "l, F d, Y g:i:s A")]}<br /></span></h3></div>';
        }
    },
    
 
    initComponent : function() {
        this.callParent(arguments);
        this.addEvents('trigger1click', 'trigger2click');
    },
    
    listerners: {
        specialkey: {
            buffer: 10,
            fn: function(combo, ev) {
                if (ev.getKey() === ev.ENTER) {
                    combo.onTrigger2Click();
                }
            }
        }
    },
    
    
    onTrigger1Click : function(){
        this.fireEvent('trigger1click',this);
    },

    onTrigger2Click : function(){
        this.fireEvent('trigger2click',this);
    }

});


