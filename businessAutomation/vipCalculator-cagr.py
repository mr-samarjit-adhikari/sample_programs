#! /usr/bin/env python

import sys,string
import getopt

from math import pow

class vipInvestment:
    """ An vipInvestment class to hold investment details"""
    def __init__(self, kwargs):
        self.navFileName = kwargs.get('navFileName',"navFile")
        self.initInvest  = kwargs.get('initialInvestment',5000)
        self.minInvest   = kwargs.get('minInvestment',1000)
        self.maxInvest   = kwargs.get('maxInvestment',10000)
        self.expectedRet = kwargs.get('expectedReturn',15)
        self.installmentCount = kwargs.get('installmentCount',12)
        self.navFile     = None

    def __validateInput(self):
        """
        Internal function to validate inputs.
        """
        try:
            self.navFile = open(self.navFileName)
        except IOError as error:
            print error
            sys.exit(1)

    def __printHdr(self):
        """
        Internal function tp print table Header.
        """
        print "NAV\t ValueAvg\t  SharedOwn\t  SharedBrought\t    CurrInvst"
        print "---\t --------\t  ---------\t  -------------\t    ---------\n"
    def __printRow(self,nav,valAvg,shareOwn,shareBro,currInvst):
        """
        Internal function to print each row of VIP
        """
        print "%.2f\t %.2f\t  %.2f  %16.2f\t  %10.2f\n"%\
              (nav,valAvg,shareOwn,shareBro,currInvst)

    def __printSummary(self,instCount,shareOwn,totalInvst):
        """
        Function to print summary of the investment.
        """
        print "Installment Count: %20d\n"\
              "Total Share Owned: %20.2f\n" \
              "Total Investment: %23.2f\n"\
              "Average Cost per share: %13.2f"\
              %(instCount,shareOwn,totalInvst,totalInvst/shareOwn)

    def findRet(self):
        """
        function to calculate return and print table.
        """
        self.__validateInput()
        self.__printHdr()
        # Now do the original calculation.
        totalRet    = 0
        annualInvst = self.initInvest * 12
        yearCount   = self.installmentCount / 12
        yearRem     = self.installmentCount % 12
        while yearCount > 0:
            totalRet += (annualInvst * \
                        (pow((1+self.expectedRet/100.0),yearCount)))
            yearCount -= 1

        #Calculate totalReturn for remaining (fractional) year
        totalRet   += ((self.initInvest * yearRem) * \
                      (pow((1+self.expectedRet/100.0),float(yearRem)/12)))
        #Now average Investment Return 
        avgInvstRet = totalRet/self.installmentCount #average installment return

        #Average value calculation
        amount     = self.initInvest;
        count      = 0
        shareOwn   = 0
        totalInvst = 0
        for nav in self.navFile:
            count    = count + 1
            navFloat = float(nav.rstrip('\n'))
            valAvg   = count*amount
            shareReq = valAvg/navFloat

            if (shareReq >= shareOwn):
                shareBro = shareReq - shareOwn
                currInvst = navFloat * shareBro
                #Check the upper Limit
                if (currInvst > self.maxInvest):
                    currInvst = self.maxInvest
                    shareBro  = currInvst/navFloat
            else:
                #Check the lower Limit
                currInvst = self.minInvest
                shareBro  = currInvst/navFloat

            # Update the list of shares
            shareOwn   += shareBro
            totalInvst += currInvst

            #print
            self.__printRow(navFloat,valAvg,shareOwn,shareBro,currInvst)
            amount = avgInvstRet

            #Exit condition of Loop
            if ((totalInvst < valAvg) and
                (count>= self.installmentCount)):
                break

        #print summary 
        self.__printSummary(count,shareOwn,totalInvst)

      
def show_help():
    print "\nThis is a VIP calculator.\n"\
          "Usage: %s [Option1]...[OptionsN]  <NAV filename>\n\n"\
          "<NAV filename>\n"\
          "        Specify a text file which contains list of NAV as follows...\n"\
          "        <NAV1>\n"\
          "        <NAV2>\n"\
          "        <...> \n\n"\
          "Options:\n"\
          " -i = --initialInvestment <amount>\n"\
          "        The initial amount with which the investment has been started.\n"\
          " -n = --minInvestment <amount>\n"\
          "        The minimum amount to be invested.\n"\
          " -a = --maxInvestment <amount>\n"\
          "        The maximum amount to be invested.\n"\
          " -e = --expectedReturn <value>\n"\
          "        The percentage(%%) of value expected on investment.\n"\
          " -m = --installmentCount <count>\n"\
          "        The installment count, the investment needs to be continued.\n"\
          %(sys.argv[0])
 
def main():
    # parse command line options
    # intialize variables.
    optionDict = {}

    try:
        optlist, args = getopt.getopt(sys.argv[1:],'hn:i:a:n:e:m:', 
                                      ['navFile=',
                                       'initialInvestment=',
                                       'maxInvestment=',
                                       'minInvestment=',
                                       'expectedReturn=',
                                       'installmentCount=',
                                       'help'])
    except getopt.error, msg:
        print "ERROR: %s"%(msg)
        show_help()
        sys.exit(1)

    # process options
    argLen = len(sys.argv)
    if (argLen < 2):
        show_help()
        sys.exit(1)

    if(len(args) != 1):
        show_help()
        sys.exit(1)

    #Assign the filename
    optionDict['navFileName'] = args[0]
      
    for opt, optarg in optlist:
        if opt in ("-h", "--help"):
            show_help()
            sys.exit(0)
        elif opt in ("-i","--initialInvestment"):
            optionDict['initialInvestment'] = string.atoi(optarg)

        elif opt in ("-n","--minInvestment"):
            optionDict['minInvestment'] = string.atoi(optarg)

        elif opt in ("-a","--maxInvestment"):
            optionDict['maxInvestment'] = string.atoi(optarg)

        elif opt in ("-e","--expectedReturn"):
            optionDict['expectedReturn'] = string.atoi(optarg)

        elif opt in ("-m","--installmentCount"):
            optionDict['installmentCount'] = string.atoi(optarg)

        else:
            #switch statment using lambda
            #value='b'
            #x=10
            #result = {
            #           'a': lambda x: x * 5,
            #           'b': lambda x: x + 7,
            #           'c': lambda x: x - 2
            #         }[value](x)
            # print "result is",result
            show_help()
           
    # Create a new VIP investment.
    vipInvst = vipInvestment(optionDict)
    vipInvst.findRet()

    for arg in args:
        #don't do anything now
        #http://docs.python.org/library/getopt.html
        pass


# main() function calling
if __name__ == "__main__":
    main()   
