/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roadbot;

/**
 *
 * @author Ravindu Weerasinghe
 */

import java.util.Random;

public class RoadBot
{
    //formula whic is used => Q(state, action) = R(state, action) + Gamma * Max[Q(next state, all actions)]
    
    private static final int Q_SIZE = 6; //size of Q matrix
    private static final double GAMMA = 0.8; //gamma value=>(0 <= Gamma > 1)
    private static final int ITERATIONS = 20; //number of iterations which run the algorithm
    private static final int INITIAL_STATES[] = new int[] {1, 3, 5, 2, 4, 0}; //these are the initial states which the robot has

    //here the R matrix values have been given. -1 is given to the paths states there is no link in-between
    //0 is given to the paths which there is link between tha states, but not closet to the resource target
    //100 is given to the paths which there is link between tha states and closet to the resource target
    
    private static final int R[][] = new int[][] {{-1, -1, -1, -1, 0, -1}, 
                                                  {-1, -1, -1, 0, -1, 100}, 
                                                  {-1, -1, -1, 0, -1, -1}, 
                                                  {-1, 0, 0, -1, 0, -1}, 
                                                  {0, -1, -1, 0, -1, 100}, 
                                                  {-1, 0, -1, -1, 0, 100}};

    private static int q[][] = new int[Q_SIZE][Q_SIZE];
    private static int currentState = 0;
    
    //method to begin the training of the robot
    private static void train()
    {
        setInitialStates();

        // Perform training, starting at all initial states.
        for(int j = 0; j < ITERATIONS; j++)
        {
            for(int i = 0; i < Q_SIZE; i++)
            {
                findTheTarget(INITIAL_STATES[i]);
            } // i
        } // j

        return;
    }
    
    private static void testPaths()
    {
        // Perform tests, starting at all initial states.
        System.out.println("Easiest paths from initial states to the resource target:");
        for(int i = 0; i < Q_SIZE; i++)
        {
            
            currentState = INITIAL_STATES[i];
            System.out.print("starting state ("+currentState+") to"+" resource target(5) => ");
            
            int newState = 0;
            do
            {
                //print the paths from each initial state to the resource target
                newState = maximum(currentState, true);
               
                System.out.print(currentState + " -> ");
                
                currentState = newState;
            }while(currentState < 5);
            System.out.print("5\n");
        }

        return;
    }
    
    //method to find the target and do convergence
    private static void findTheTarget(final int initialState)
    {
        currentState = initialState;

        // Travel from state to state until target state is reached.
        do
        {
            takeDecision();
        }while(currentState == 5);

        // When currentState = 5, Run through the set once more for convergence.
        for(int i = 0; i < Q_SIZE; i++)
        {
            takeDecision();
        }
        return;
    }
    
    //take decisions which is connected to current state
    private static void takeDecision()
    {
        int possibleAction = 0;

        // Randomly choose a possible action connected to the current state.
        possibleAction = getRandomAction(Q_SIZE);

        if(R[currentState][possibleAction] >= 0){
            q[currentState][possibleAction] = reward(possibleAction);
            currentState = possibleAction;
        }
        return;
    }
    
    private static int getRandomAction(final int upperBound)
    {
        int action = 0;
        boolean choiceIsValid = false;

        // Randomly choose a possible action connected to the current state.
        while(choiceIsValid == false)
        {
            // Get a random value between 0(inclusive) and 6(exclusive).
            action = new Random().nextInt(upperBound);
            if(R[currentState][action] > -1){
                choiceIsValid = true;
            }
        }

        return action;
    }
    
    //set the initial states to zero
    private static void setInitialStates()
    {
        for(int i = 0; i < Q_SIZE; i++)
        {
            for(int j = 0; j < Q_SIZE; j++)
            {
                q[i][j] = 0;
            } // j
        } // i
        return;
    }
    
    //return the Q index
    private static int maximum(final int State, final boolean ReturnIndexOnly)
    {
        // If ReturnIndexOnly = True, the Q matrix index is returned.
        // If ReturnIndexOnly = False, the Q matrix value is returned.
        int maximum_selection = 0;
        boolean foundNewSelection = false;
        boolean done = false;

        while(!done)
        {
            foundNewSelection = false;
            for(int i = 0; i < Q_SIZE; i++)
            {
                if(i != maximum_selection){             // Avoid self-comparison.
                    if(q[State][i] > q[State][maximum_selection]){
                        maximum_selection = i;
                        foundNewSelection = true;
                    }
                }
            }

            if(foundNewSelection == false){
                done = true;
            }
        }

        if(ReturnIndexOnly == true){
            return maximum_selection;
        }else{
            return q[State][maximum_selection];
        }
    }
    
    private static int reward(final int Action)
    {
        return (int)(R[currentState][Action] + (GAMMA * maximum(Action, false)));
    }
    
    //main method
    public static void main(String[] args)
    {
        train();
        testPaths();
        return;
    }

}