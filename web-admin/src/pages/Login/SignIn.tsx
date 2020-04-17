import React from 'react'
import * as firebase from 'firebase/app'
import * as firebaseui from 'firebaseui'
import Container from '@material-ui/core/Container'
import Grid from '@material-ui/core/Grid'
import StyledFirebaseAuth from 'react-firebaseui/StyledFirebaseAuth'

import { auth } from 'infrastructure/firebase'
import { makeStyles } from '@material-ui/core'

const useStyles = makeStyles((theme) => ({
  root: {
    marginTop: '30vh',
  },
}))

const uiConfig = {
  signInFlow: 'popup',
  signInOptions: [firebase.auth.EmailAuthProvider.PROVIDER_ID],
  credentialHelper: firebaseui.auth.CredentialHelper.NONE,
}

function SignIn() {
  const classes = useStyles()

  return (
    <Container className={classes.root} fixed maxWidth="xs">
      <Grid container alignItems="center" direction="column">
        <Grid item>
          <StyledFirebaseAuth uiConfig={uiConfig} firebaseAuth={auth} />
        </Grid>
      </Grid>
    </Container>
  )
}

export default SignIn
