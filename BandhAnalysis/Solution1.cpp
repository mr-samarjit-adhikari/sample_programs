#include <iostream>
#include <vector>
#include <set>
#include <list>

using namespace std;
#define DAYS_INTERVAL 7

class Party{
    public:
        Party(unsigned int bparam,unsigned long days);
        ~Party();
        unsigned int getBandhParam(){ return bparam;}
        unsigned long getTotDaysCount(){return totDaysCount;}
    private:
        unsigned int bparam;
        unsigned long totDaysCount;
};

Party::Party(unsigned int bp,unsigned long days)
      :bparam(bp),totDaysCount(days){}
Party::~Party(){}

void 
processPartyInput(list<vector<Party> >& testCasesList,unsigned long daysCount,
                  unsigned int partyCount)
{
    unsigned int bp = 0;
    vector<Party> *partyList = new vector<Party>();

    for(int idx=0;idx<partyCount;idx++){
        cin>>bp;
        Party *p = new Party(bp,daysCount);
        partyList->push_back(*p);
    }

    testCasesList.push_back(*partyList);
}

unsigned int
processOneTestCase(vector<Party>& partyList)
{
    vector<Party>::iterator it;
    unsigned long daysCount = partyList[0].getTotDaysCount();
    set<unsigned long>      workingSet;
    
    for(it=partyList.begin();it!=partyList.end();it++){
        Party  pr = *it;
        unsigned int  bp         = pr.getBandhParam();
        unsigned long index      = 1;
        unsigned long condVal    = index*bp;

        while(condVal <= daysCount){

            unsigned int rd = (condVal%DAYS_INTERVAL); // Remaining Days
            if(rd>0 && rd <(DAYS_INTERVAL-1)){ // Sat-Sun exclude
                workingSet.insert(index*bp);
            }
            index++;
            condVal = (index*bp);
        }
    }

    return workingSet.size();
}

// MAIN -  Function
main()
{
    list<vector<Party> >  testCasesList;
    list<vector<Party> >::iterator it;
    unsigned int testCases  = 0;
    unsigned long daysCount = 0;
    unsigned int  partyCount = 0;
    unsigned int ret        = 0;

    // Starts getting Input
    cin>>testCases;
    
    for(int index=0;index<testCases;index++){
        cin>>daysCount;
        cin>>partyCount;
        processPartyInput(testCasesList,daysCount,partyCount);
    }

    for(it = testCasesList.begin(); it!=testCasesList.end();it++){
        ret = processOneTestCase(*it);
        cout<<ret<<endl;
    }
}
