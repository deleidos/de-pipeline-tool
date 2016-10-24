/*describe ('UI testing with mockup', function() {
 beforeEach(function() {

 });
 });*/

(function() {

    'use strict';

    describe('initial tests', function() {

        beforeEach(function() {
            browser.get('http://pipeline-dev.deleidos.com');

        });

        it('Title present: indicates website is functional', function() {
            /*var page = require('webpage').create();
             page.open('http://pipeline-dev.deleidos.com', function () {
             var title = page.evaluate(function () {
             return document.title;
             });
             expect(title).toEqual('DigitalEdge Pipeline Tool');*/
            expect(browser.getTitle()).toEqual('DigitalEdge Pipeline Tool');
            //});
        });

                it('Operators present: indicates data service and Mongo are functional', function() {
         var opList = element.all(by.repeater('opType in displayList'));
         expect(opList.count()).toBeGreaterThan(0);
         });

                it('Systems present: indicates Hadoop is working right', function() {
         var sysList = element.all(by.repeater('(index, system) in systems'));
         expect(sysList.count()).toBeGreaterThan(0);
         });

        /*        it('operators should be present', function() {


         var ops = element.all(by.repeater('operator in opType.operators'));
         expect(ops.count()).toBeGreaterThan(0);
         });

         */
        //element(by.model('searchOp.display_name')).sendKeys('output');
        //element(by.operatorButton('Input')).click();

        //element(by.cssContainingText('.ng-binding', 'Input')).click();

        /*element.all(by.css('md-select')).each(function (eachElement, index) {
         eachElement.click();                    //select the select
         browser.driver.sleep(500);              //wait for the renderings to take effect
         element(by.css('md-option')).click();   //select the first md-option
         browser.driver.sleep(500);              //wait for the renderings to take effect
         });*/

        //expect(ops.count()).toBe(4);

        /*var button = browser.findElement(by.cssContainingText('.md-tab', 'Input'));

         button.click();*/
    });


    /*
     describe('Protractor Demo App', function() {
     var firstNumber = element(by.model('first'));
     var secondNumber = element(by.model('second'));
     var goButton = element(by.id('gobutton'));
     var latestResult = element(by.binding('latest'));
     var history = element.all(by.repeater('result in memory'));

     function add(a, b) {
     firstNumber.sendKeys(a);
     secondNumber.sendKeys(b);
     goButton.click();
     }

     beforeEach(function() {
     browser.get('http://juliemr.github.io/protractor-demo/');
     });

     it('should have a history', function() {
     add(1, 2);
     add(3, 4);

     expect(history.count()).toEqual(2);

     add(5, 6);
     console.log(history.count());
     expect(history.count()).toEqual(0); // This is wrong!
     });
     });
     */
}());
